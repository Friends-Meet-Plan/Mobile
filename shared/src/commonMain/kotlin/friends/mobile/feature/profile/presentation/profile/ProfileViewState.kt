package friends.mobile.feature.profile.presentation.profile

import friends.mobile.feature.profile.domain.model.Profile

sealed class ProfileViewState {
    data object Loading : ProfileViewState()
    data class Error(val message: String) : ProfileViewState()
    data class Content(
        val profile: Profile,
        val isLoggingOut: Boolean = false,
        val isRefreshing: Boolean = false,
    ) : ProfileViewState()
}
