package friends.mobile.feature.profile.presentation.edit

sealed class EditProfileViewState {
    data object Loading : EditProfileViewState()

    data class Error(val message: String) : EditProfileViewState()

    data class Content(
        val username: String = "",
        val bio: String = "",
        val avatarUrl: String = "",
        val isSaving: Boolean = false
    ) : EditProfileViewState()
}
