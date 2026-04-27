package friends.mobile.feature.auth.data.storage

import friends.mobile.feature.auth.domain.model.AuthSession

/** Persists and retrieves the current [friends.mobile.feature.auth.domain.model.AuthSession] on-device. */
interface TokenStorage {
    fun saveSession(session: friends.mobile.feature.auth.domain.model.AuthSession)
    fun getSession(): friends.mobile.feature.auth.domain.model.AuthSession?
    fun clearSession()
}
