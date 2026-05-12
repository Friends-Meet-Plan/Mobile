package friends.mobile.feature.profile.presentation.profile

sealed class ProfileEvent {
    object OnLoadProfile : ProfileEvent()
    object OnLogoutClick : ProfileEvent()
}
