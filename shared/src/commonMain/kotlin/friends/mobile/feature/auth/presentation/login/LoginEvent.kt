package friends.mobile.feature.auth.presentation.login

sealed class LoginEvent {
    data class OnUsernameChanged(val value: String) : LoginEvent()
    data class OnPasswordChanged(val value: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
}
