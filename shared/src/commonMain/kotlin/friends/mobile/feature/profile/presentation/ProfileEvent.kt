package friends.mobile.feature.profile.presentation

sealed class ProfileEvent {
    object OnLoadProfile : ProfileEvent()
    object OnLogoutClick : ProfileEvent()
}
