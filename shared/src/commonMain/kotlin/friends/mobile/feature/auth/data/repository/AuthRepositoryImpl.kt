package friends.mobile.feature.auth.data.repository

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.auth.data.mapper.AuthSessionMapper
import friends.mobile.feature.auth.data.remote.AuthApi
import friends.mobile.feature.auth.data.remote.dto.LoginRequestDto
import friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto
import friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto
import friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.feature.auth.domain.storage.TokenStorage

private const val HTTP_UNAUTHORIZED = 401

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
    ): ResultWrapper<Unit> = safeApiCall {
        api.register(RegisterRequestDto(username, password, avatarUrl, bio))
    }

    override suspend fun login(
        username: String,
        password: String
    ): ResultWrapper<AuthSession> = safeApiCall {
        val response = api.login(LoginRequestDto(username, password))
        val session = mapper.loginResponseToDomain(response)
        storage.saveSession(session)
        session
    }

    override suspend fun refresh(): ResultWrapper<AuthToken> {
        val current = storage.getSession()
            ?: return ResultWrapper.Error(ApiError(HTTP_UNAUTHORIZED, "Your session has expired. Please log in again."))

        return safeApiCall {
            val response = api.refresh(RefreshRequestDto(current.token.refreshToken))
            val newToken = mapper.refreshResponseToDomainToken(response)
            val updatedSession = current.copy(token = newToken)
            storage.saveSession(updatedSession)
            newToken
        }
    }

    override suspend fun logout(): ResultWrapper<Unit> {
        val session = storage.getSession()
        storage.clearSession()
        if (session == null) return ResultWrapper.Success(Unit)

        return safeApiCall {
            api.logout(LogoutRequestDto(session.token.refreshToken))
        }
    }

    override fun getStoredSession(): AuthSession? =
        storage.getSession()

    override fun isAuthenticated(): Boolean =
        storage.getSession() != null
}
