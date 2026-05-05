package friends.mobile.feature.auth.presentation.register

sealed class RegisterAction {
    data object RegisterSucceeded : RegisterAction()
    data class ShowMessage(val message: String) : RegisterAction()
}

