package friends.mobile.auth.storage

import friends.mobile.auth.model.AuthSession

/** Persists and retrieves the current [AuthSession] on-device. */
interface TokenStorage {
    fun saveSession(session: AuthSession)
    fun getSession(): AuthSession?
    fun clearSession()
}
