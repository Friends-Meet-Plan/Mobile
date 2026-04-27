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
) : friends.mobile.feature.auth.data.storage.TokenStorage {

    override fun saveSession(session: friends.mobile.feature.auth.domain.model.AuthSession) {
        settings[_root_ide_package_.friends.mobile.feature.auth.data.storage.KEY] = json.encodeToString(session.toStoredSessionDto())
    }

    override fun getSession(): friends.mobile.feature.auth.domain.model.AuthSession? =
        settings.getStringOrNull(_root_ide_package_.friends.mobile.feature.auth.data.storage.KEY)?.let { raw ->
            runCatching { json.decodeFromString<friends.mobile.feature.auth.data.storage.dto.StoredSessionDto>(raw) }
                .getOrNull()
                ?.toAuthSession()
        }

    override fun clearSession() {
        settings.remove(_root_ide_package_.friends.mobile.feature.auth.data.storage.KEY)
    }
}

private fun friends.mobile.feature.auth.domain.model.AuthSession.toStoredSessionDto(): friends.mobile.feature.auth.data.storage.dto.StoredSessionDto =
    _root_ide_package_.friends.mobile.feature.auth.data.storage.dto.StoredSessionDto(
        accessToken = token.accessToken,
        refreshToken = token.refreshToken,
        userId = user.id,
        username = user.username,
        avatarUrl = user.avatarUrl,
        bio = user.bio,
    )

private fun friends.mobile.feature.auth.data.storage.dto.StoredSessionDto.toAuthSession(): friends.mobile.feature.auth.domain.model.AuthSession =
    _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthSession(
        token = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthToken(
            accessToken,
            refreshToken
        ),
        user = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthUser(
            userId,
            username,
            avatarUrl,
            bio
        ),
    )
