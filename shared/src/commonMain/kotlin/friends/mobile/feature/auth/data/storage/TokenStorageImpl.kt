package friends.mobile.feature.auth.data.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import friends.mobile.feature.auth.data.storage.dto.StoredSessionDto
import friends.mobile.feature.auth.data.storage.toAuthSession
import friends.mobile.feature.auth.data.storage.toStoredSessionDto
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.model.AuthUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val KEY = "auth_session"

class TokenStorageImpl(
    private val settings: Settings,
    private val json: Json,
) : TokenStorage {

    override fun saveSession(session: AuthSession) {
        settings[KEY] = json.encodeToString(session.toStoredSessionDto())
    }

    override fun getSession(): AuthSession? =
        settings.getStringOrNull(KEY)?.let { raw ->
            runCatching { json.decodeFromString<StoredSessionDto>(raw) }
                .getOrNull()
                ?.toAuthSession()
        }

    override fun clearSession() {
        settings.remove(KEY)
    }
}

private fun AuthSession.toStoredSessionDto(): StoredSessionDto =
    StoredSessionDto(
        accessToken = token.accessToken,
        refreshToken = token.refreshToken,
        userId = user.id,
        username = user.username,
        avatarUrl = user.avatarUrl,
        bio = user.bio,
    )

private fun StoredSessionDto.toAuthSession(): AuthSession =
    AuthSession(
        token = AuthToken(accessToken, refreshToken),
        user = AuthUser(userId, username, avatarUrl, bio),
    )
