package friends.mobile.auth.repository

import friends.mobile.auth.exception.AuthException
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
        val r = api.login(LoginRequestDto(username, password))
        val session = AuthSession(
            token = AuthToken(r.accessToken, r.refreshToken),
            user  = AuthUser(r.user.id, r.user.username, r.user.avatarUrl, r.user.bio),
        )
        storage.saveSession(session)
        return session
    }

    override suspend fun refresh(): AuthToken {
        val current = storage.getSession() ?: throw AuthException.Unauthorized
        val r = api.refresh(RefreshRequestDto(current.token.refreshToken))
        val newToken = AuthToken(r.accessToken, r.refreshToken)
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
