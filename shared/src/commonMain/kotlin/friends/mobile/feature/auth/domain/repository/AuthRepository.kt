package friends.mobile.feature.auth.domain.repository

import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken

/** All authentication operations exposed to the domain and UI layers. */
interface AuthRepository {
    suspend fun register(username: String, password: String, avatarUrl: String? = null, bio: String? = null)
    suspend fun login(username: String, password: String): AuthSession
    suspend fun refresh(): AuthToken
    suspend fun logout()
    fun getStoredSession(): AuthSession?
    fun isAuthenticated(): Boolean
}
