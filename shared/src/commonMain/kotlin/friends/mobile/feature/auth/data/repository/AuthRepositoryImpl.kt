package friends.mobile.feature.auth.data.repository

import friends.mobile.core.network.NetworkException
import friends.mobile.feature.auth.data.mapper.AuthSessionMapper
import friends.mobile.feature.auth.data.remote.AuthApi
import friends.mobile.feature.auth.data.remote.dto.LoginRequestDto
import friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto
import friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto
import friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto
import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val storage: TokenStorage,
    private val mapper: AuthSessionMapper,
) : AuthRepository {

    override suspend fun register(
        username: String,
        password: String,
        avatarUrl: String?,
        bio: String?,
    ) {
        api.register(RegisterRequestDto(username, password, avatarUrl, bio))
    }

    override suspend fun login(
        username: String,
        password: String
    ): AuthSession {
        val response = api.login(LoginRequestDto(username, password))
        val session = mapper.loginResponseToDomain(response)

        storage.saveSession(session)

        return session
    }

    override suspend fun refresh(): AuthToken {
        val current = storage.getSession() ?: throw NetworkException.Unauthorized

        val response = api.refresh(RefreshRequestDto(current.token.refreshToken))
        val newToken = mapper.refreshResponseToDomainToken(response)

        val updatedSession = current.copy(token = newToken)
        storage.saveSession(updatedSession)

        return newToken
    }

    override suspend fun logout() {
        val session = storage.getSession() ?: return

        storage.clearSession()

        runCatching {
            api.logout(LogoutRequestDto(session.token.refreshToken))
        }
    }

    override fun getStoredSession(): AuthSession? =
        storage.getSession()

    override fun isAuthenticated(): Boolean =
        storage.getSession() != null
}
