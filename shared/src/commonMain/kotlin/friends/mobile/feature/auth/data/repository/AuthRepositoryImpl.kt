package friends.mobile.feature.auth.data.repository

import friends.mobile.feature.auth.data.remote.AuthApi
import friends.mobile.feature.auth.data.remote.dto.LoginRequestDto
import friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto
import friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto
import friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto
import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.model.AuthUser
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.core.network.NetworkException

/** Coordinates [friends.mobile.feature.auth.data.remote.AuthApi] calls with [friends.mobile.feature.auth.data.storage.TokenStorage]; handles token rotation on refresh. */
class AuthRepositoryImpl(
    private val api: friends.mobile.feature.auth.data.remote.AuthApi,
    private val storage: friends.mobile.feature.auth.data.storage.TokenStorage,
) : friends.mobile.feature.auth.domain.repository.AuthRepository {

    override suspend fun register(username: String, password: String, avatarUrl: String?, bio: String?) {
        api.register(
            _root_ide_package_.friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto(
                username,
                password,
                avatarUrl,
                bio
            )
        )
    }

    override suspend fun login(username: String, password: String): friends.mobile.feature.auth.domain.model.AuthSession {
        val response = api.login(
            _root_ide_package_.friends.mobile.feature.auth.data.remote.dto.LoginRequestDto(
                username,
                password
            )
        )
        val session = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthSession(
            token = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthToken(
                response.accessToken,
                response.refreshToken
            ),
            user = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthUser(
                response.user.id,
                response.user.username,
                response.user.avatarUrl,
                response.user.bio
            ),
        )
        storage.saveSession(session)
        return session
    }

    override suspend fun refresh(): friends.mobile.feature.auth.domain.model.AuthToken {
        val current = storage.getSession() ?: throw NetworkException.Unauthorized
        val response = api.refresh(
            _root_ide_package_.friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto(
                current.token.refreshToken
            )
        )
        val newToken = _root_ide_package_.friends.mobile.feature.auth.domain.model.AuthToken(
            response.accessToken,
            response.refreshToken
        )
        storage.saveSession(current.copy(token = newToken))
        return newToken
    }

    override suspend fun logout() {
        val session = storage.getSession() ?: return
        storage.clearSession()
        runCatching { api.logout(
            _root_ide_package_.friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto(
                session.token.refreshToken
            )
        ) }
    }

    override fun getStoredSession(): friends.mobile.feature.auth.domain.model.AuthSession? = storage.getSession()
    override fun isAuthenticated(): Boolean = storage.getSession() != null
}
