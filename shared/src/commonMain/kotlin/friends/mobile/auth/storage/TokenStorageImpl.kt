package friends.mobile.auth.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import friends.mobile.auth.model.AuthSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val KEY = "auth_session"

/** [TokenStorage] backed by Multiplatform-Settings (SharedPreferences / NSUserDefaults). */
class TokenStorageImpl(
    private val settings: Settings,
    private val json: Json,
) : TokenStorage {

    override fun saveSession(session: AuthSession) {
        settings[KEY] = json.encodeToString(session)
    }

    override fun getSession(): AuthSession? =
        settings.getStringOrNull(KEY)?.let { runCatching { json.decodeFromString<AuthSession>(it) }.getOrNull() }

    override fun clearSession() {
        settings.remove(KEY)
    }
}
