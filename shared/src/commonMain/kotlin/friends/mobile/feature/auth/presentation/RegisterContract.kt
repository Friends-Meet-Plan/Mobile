package friends.mobile.feature.auth.presentation

sealed interface RegisterAction

class RegisterSucceeded : friends.mobile.feature.auth.presentation.RegisterAction

sealed interface RegisterEvent

data class OnRegisterClick(
    val username: String,
    val password: String,
) : friends.mobile.feature.auth.presentation.RegisterEvent
