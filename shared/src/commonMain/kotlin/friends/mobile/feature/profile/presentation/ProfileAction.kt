package friends.mobile.feature.profile.presentation

sealed class ProfileAction {
    object LogoutRequested : ProfileAction()
    data class ShowMessage(val message: String) : ProfileAction()
}
