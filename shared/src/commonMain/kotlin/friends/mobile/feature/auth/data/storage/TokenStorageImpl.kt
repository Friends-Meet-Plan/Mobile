package friends.mobile.feature.auth.data.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import friends.mobile.feature.auth.data.mapper.StoredSessionMapper
import friends.mobile.feature.auth.data.storage.dto.StoredSessionDto
import friends.mobile.feature.auth.domain.model.AuthSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val KEY = "auth_session"

internal class TokenStorageImpl(
    private val settings: Settings,
    private val json: Json,
    private val mapper: StoredSessionMapper,
) : TokenStorage {

    override fun saveSession(session: AuthSession) {
        val dto = mapper.domainSessionToStoredSessionDto(session)
        settings[KEY] = json.encodeToString(dto)
    }

    override fun getSession(): AuthSession? =
        settings.getStringOrNull(KEY)?.let { raw ->
            runCatching {
                json.decodeFromString<StoredSessionDto>(raw)
            }.getOrNull()?.let(mapper::storedSessionDtoToDomain)
        }

    override fun clearSession() {
        settings.remove(KEY)
    }
}