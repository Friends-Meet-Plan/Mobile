package friends.mobile.feature.auth.presentation.login

sealed class LoginViewState {
    data object Loading : LoginViewState()
    data class Error(val message: String) : LoginViewState()
    data class Content(
        val username: String = "",
        val password: String = "",
        val isLoggingIn: Boolean = false
    ) : LoginViewState()
}
