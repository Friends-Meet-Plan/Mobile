package friends.mobile.feature.auth.domain.storage

import friends.mobile.feature.auth.domain.model.AuthSession

interface TokenStorage {
    fun saveSession(session: AuthSession)
    fun getSession(): AuthSession?
    fun clearSession()
}
