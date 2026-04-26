package friends.mobile.auth.repository

import friends.mobile.network.NetworkException
import friends.mobile.auth.model.AuthSession
import friends.mobile.auth.model.AuthToken
import friends.mobile.auth.model.AuthUser
import friends.mobile.auth.remote.AuthApi
import friends.mobile.auth.remote.dto.LoginRequestDto
import friends.mobile.auth.remote.dto.LogoutRequestDto
import friends.mobile.auth.remote.dto.RefreshRequestDto
import friends.mobile.auth.remote.dto.RegisterRequestDto
import friends.mobile.auth.storage.TokenStorage

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
            user  = AuthUser(response.user.id, response.user.username, response.user.avatarUrl, response.user.bio),
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
