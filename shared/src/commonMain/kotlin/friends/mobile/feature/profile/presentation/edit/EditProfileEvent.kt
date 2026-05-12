package friends.mobile.feature.profile.presentation.edit

sealed class EditProfileEvent {

    data class Init(val username: String, val bio: String, val avatarUrl: String) : EditProfileEvent()

    data class OnUsernameChanged(val value: String) : EditProfileEvent()
    data class OnBioChanged(val value: String) : EditProfileEvent()
    data class OnAvatarUrlChanged(val value: String) : EditProfileEvent()

    object OnSaveClick : EditProfileEvent()
    object OnBackClick : EditProfileEvent()
}
