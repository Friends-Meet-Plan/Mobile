package friends.mobile.feature.profile.presentation.profile

import friends.mobile.feature.profile.domain.model.Profile

sealed class ProfileAction {
    data object NavigateToLogin : ProfileAction()
    data class NavigateToEdit(val profile: Profile) : ProfileAction()
    data class ShowMessage(val message: String) : ProfileAction()
}
