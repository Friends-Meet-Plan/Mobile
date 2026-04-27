package friends.mobile.feature.auth.data.remote

import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.auth.domain.model.AuthToken
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val REFRESH_THRESHOLD_SECONDS = 60L

/**
 * Installs proactive token refresh and single-flight 401-retry logic onto [client].
 * Proactively refreshes if the access token expires within 60 s.
 * On 401, attempts one refresh (Mutex-guarded, check-after-wait), then retries.
 */
class AuthenticatedClient(
    private val client: HttpClient,
    private val storage: friends.mobile.feature.auth.data.storage.TokenStorage,
    private val onRefresh: suspend () -> friends.mobile.feature.auth.domain.model.AuthToken,
    private val onUnauthorized: suspend () -> Unit,
) {
    private val mutex = Mutex()

    init {
        client.plugin(HttpSend).intercept { request ->
            // Proactive: refresh before sending if token is near expiry
            storage.getSession()?.token?.let { token ->
                if (isExpiringSoon(token.accessToken)) {
                    mutex.withLock {
                        val latest = storage.getSession()?.token
                        if (latest != null && isExpiringSoon(latest.accessToken)) {
                            runCatching { onRefresh() }.onFailure { onUnauthorized() }
                        }
                    }
                }
            }

            // Inject current access token
            storage.getSession()?.token?.accessToken?.let {
                request.headers.set(HttpHeaders.Authorization, "Bearer $it")
            }

            val call = execute(request)

            // Reactive: on 401, refresh once and retry
            if (call.response.status == HttpStatusCode.Unauthorized) {
                val usedToken = call.request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
                val newToken = mutex.withLock {
                    val stored = storage.getSession()?.token
                    if (stored != null && stored.accessToken != usedToken) {
                        // Another coroutine already refreshed; reuse the new token
                        stored
                    } else {
                        runCatching { onRefresh() }.getOrElse { onUnauthorized(); return@intercept call }
                    }
                }
                request.headers.set(HttpHeaders.Authorization, "Bearer ${newToken.accessToken}")
                execute(request)
            } else {
                call
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun isExpiringSoon(jwt: String): Boolean = try {
        val raw = jwt.split(".").getOrNull(1) ?: return true
        val padding = (4 - raw.length % 4) % 4
        val payload = Base64.UrlSafe.decode(raw + "=".repeat(padding)).decodeToString()
        val exp = Json.parseToJsonElement(payload).jsonObject["exp"]?.jsonPrimitive?.long ?: return true
        exp <= Clock.System.now().epochSeconds + _root_ide_package_.friends.mobile.feature.auth.data.remote.REFRESH_THRESHOLD_SECONDS
    } catch (_: Exception) {
        true
    }
}
