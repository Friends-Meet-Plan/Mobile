package friends.mobile.feature.profile.presentation.edit

sealed class EditProfileAction {
    object NavigateBack : EditProfileAction()
    data class ShowMessage(val message: String) : EditProfileAction()
}
