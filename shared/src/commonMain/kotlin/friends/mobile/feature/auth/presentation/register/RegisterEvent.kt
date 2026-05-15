package friends.mobile.feature.auth.presentation.register

sealed class RegisterEvent {
    data class OnUsernameChanged(val value: String) : RegisterEvent()
    data class OnPasswordChanged(val value: String) : RegisterEvent()
    data object OnRegisterClick : RegisterEvent()
}
