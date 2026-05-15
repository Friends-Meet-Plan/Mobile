package friends.mobile.feature.friends.presentation.friends

sealed class FriendsEvent {
    data object ScreenOpened : FriendsEvent()
    data object ReloadCurrentTab : FriendsEvent()
    data class OnTabSelected(val tab: RequestTab) : FriendsEvent()
    data class OnSearchUsers(val query: String) : FriendsEvent()
    data class OnSendRequest(val friendId: String) : FriendsEvent()
    data class OnAcceptRequest(val requestId: String) : FriendsEvent()
    data class OnRejectRequest(val requestId: String) : FriendsEvent()
    data class OnCancelRequest(val requestId: String) : FriendsEvent()
    data class OnUserClick(val userId: String) : FriendsEvent()
}
