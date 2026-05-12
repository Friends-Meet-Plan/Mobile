package friends.mobile.feature.profile.presentation.profile

sealed class ProfileAction {
    object LogoutRequested : ProfileAction()
    data class ShowMessage(val message: String) : ProfileAction()
}
