package friends.mobile.auth.state

import friends.mobile.auth.model.AuthSession

/** All observable authentication states exposed via StateFlow. */
sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val session: AuthSession) : AuthState()
}
