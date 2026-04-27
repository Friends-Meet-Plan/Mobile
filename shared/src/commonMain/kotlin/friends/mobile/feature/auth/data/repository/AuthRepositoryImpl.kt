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

/** Coordinates [AuthApi] calls with [TokenStorage]; handles token rotation on refresh. */
class AuthRepositoryImpl(
    private val api: AuthApi,
    private val storage: TokenStorage,
) : AuthRepository {

    override suspend fun register(username: String, password: String, avatarUrl: String?, bio: String?) {
        api.register(RegisterRequestDto(username, password, avatarUrl, bio))
    }

    override suspend fun login(username: String, password: String): AuthSession {
        val response = api.login(LoginRequestDto(username, password))
        val session = AuthSession(
            token = AuthToken(response.accessToken, response.refreshToken),
            user = AuthUser(
                response.user.id,
                response.user.username,
                response.user.avatarUrl,
                response.user.bio
            ),
        )
        storage.saveSession(session)
        return session
    }

    override suspend fun refresh(): AuthToken {
        val current = storage.getSession() ?: throw NetworkException.Unauthorized
        val response = api.refresh(RefreshRequestDto(current.token.refreshToken))
        val newToken = AuthToken(response.accessToken, response.refreshToken)
        storage.saveSession(current.copy(token = newToken))
        return newToken
    }

    override suspend fun logout() {
        val session = storage.getSession() ?: return
        storage.clearSession()
        runCatching { api.logout(LogoutRequestDto(session.token.refreshToken)) }
    }

    override fun getStoredSession(): AuthSession? = storage.getSession()
    override fun isAuthenticated(): Boolean = storage.getSession() != null
}
