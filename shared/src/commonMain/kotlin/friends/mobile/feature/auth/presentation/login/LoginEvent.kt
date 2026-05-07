package friends.mobile.feature.auth.presentation.login

sealed class LoginEvent {
    data class OnLoginClick(
        val username: String,
        val password: String,
    ) : LoginEvent()
}
