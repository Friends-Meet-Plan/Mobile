package friends.mobile.feature.auth.domain.repository

import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken

/** All authentication operations exposed to the domain and UI layers. */
interface AuthRepository {
    suspend fun register(username: String, password: String, avatarUrl: String? = null, bio: String? = null)
    suspend fun login(username: String, password: String): friends.mobile.feature.auth.domain.model.AuthSession
    suspend fun refresh(): friends.mobile.feature.auth.domain.model.AuthToken
    suspend fun logout()
    fun getStoredSession(): friends.mobile.feature.auth.domain.model.AuthSession?
    fun isAuthenticated(): Boolean
}
