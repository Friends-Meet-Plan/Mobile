package friends.mobile.feature.profile.presentation

import friends.mobile.feature.profile.domain.model.Profile

data class ProfileViewState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
