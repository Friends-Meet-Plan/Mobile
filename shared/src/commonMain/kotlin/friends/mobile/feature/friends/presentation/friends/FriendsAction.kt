package friends.mobile.feature.friends.presentation.friends

sealed class FriendsAction {
    data class ShowError(val message: String) : FriendsAction()
    data class NavigateToFriendProfile(val userId: String) : FriendsAction()
}
