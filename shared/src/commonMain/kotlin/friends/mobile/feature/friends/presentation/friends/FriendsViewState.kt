package friends.mobile.feature.friends.presentation.friends

import friends.mobile.feature.friends.domain.model.User

enum class RequestTab {
    FRIENDS,
    INCOMING,
    OUTGOING,
}

sealed class FriendsViewState {
    data object Loading : FriendsViewState()
    data class Error(val message: String) : FriendsViewState()
    data class Content(
        val currentTab: RequestTab = RequestTab.FRIENDS,
        val searchText: String = "",
        val friendsList: List<User> = emptyList(),
        val incomingRequests: List<User> = emptyList(),
        val outgoingRequests: List<User> = emptyList(),
        val searchResults: List<User>? = null,
        val isSearching: Boolean = false,
        val isActionPending: Boolean = false,
        val isTabLoading: Boolean = false
    ) : FriendsViewState()
}
