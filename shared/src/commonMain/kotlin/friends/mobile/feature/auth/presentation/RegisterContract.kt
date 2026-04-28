package friends.mobile.feature.auth.presentation

sealed interface RegisterAction

class RegisterSucceeded : RegisterAction

sealed interface RegisterEvent

data class OnRegisterClick(
    val username: String,
    val password: String,
) : RegisterEvent
