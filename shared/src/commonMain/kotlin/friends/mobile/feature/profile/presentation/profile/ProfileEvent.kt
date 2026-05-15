package friends.mobile.feature.profile.presentation.profile

sealed class ProfileEvent {
    data object OnLoadProfile : ProfileEvent()
    data object OnRefreshProfile : ProfileEvent()
    data object OnLogoutClick : ProfileEvent()
    data object OnEditClick : ProfileEvent()
}
