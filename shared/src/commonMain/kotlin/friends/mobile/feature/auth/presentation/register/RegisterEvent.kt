package friends.mobile.feature.auth.presentation.register

sealed class RegisterEvent {
    data class OnRegisterClick(
        val username: String,
        val password: String,
    ) : RegisterEvent()
}
