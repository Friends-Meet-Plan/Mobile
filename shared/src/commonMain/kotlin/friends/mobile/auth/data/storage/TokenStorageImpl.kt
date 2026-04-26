package friends.mobile.auth.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import friends.mobile.auth.model.AuthSession
import friends.mobile.auth.model.AuthToken
import friends.mobile.auth.model.AuthUser
import friends.mobile.auth.storage.dto.StoredSessionDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val KEY = "auth_session"
// TODO: вынести маппер
class TokenStorageImpl(
    private val settings: Settings,
    private val json: Json,
) : TokenStorage {

    override fun saveSession(session: AuthSession) {
        val dto = StoredSessionDto(
            accessToken = session.token.accessToken,
            refreshToken = session.token.refreshToken,
            userId = session.user.id,
            username = session.user.username,
            avatarUrl = session.user.avatarUrl,
            bio = session.user.bio,
        )
        settings[KEY] = json.encodeToString(dto)
    }

    override fun getSession(): AuthSession? =
        settings.getStringOrNull(KEY)?.let { raw ->
            runCatching { json.decodeFromString<StoredSessionDto>(raw) }
                .getOrNull()
                ?.let { dto ->
                    AuthSession(
                        token = AuthToken(dto.accessToken, dto.refreshToken),
                        user  = AuthUser(dto.userId, dto.username, dto.avatarUrl, dto.bio),
                    )
                }
        }

    override fun clearSession() {
        settings.remove(KEY)
    }
}
