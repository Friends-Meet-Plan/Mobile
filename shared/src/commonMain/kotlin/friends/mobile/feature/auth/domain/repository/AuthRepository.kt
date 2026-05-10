package friends.mobile.feature.auth.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken

/** All authentication operations exposed to the domain and UI layers. */
interface AuthRepository {
    suspend fun register(
        username: String,
        password: String,
        avatarUrl: String? = null,
        bio: String? = null,
    ): ResultWrapper<Unit>
    suspend fun login(username: String, password: String): ResultWrapper<AuthSession>
    suspend fun refresh(): ResultWrapper<AuthToken>
    suspend fun logout(): ResultWrapper<Unit>
    fun getStoredSession(): AuthSession?
    fun isAuthenticated(): Boolean
}
