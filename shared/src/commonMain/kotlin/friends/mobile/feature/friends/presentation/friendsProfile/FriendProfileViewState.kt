package friends.mobile.feature.friends.presentation.friendsProfile

import friends.mobile.feature.friends.domain.model.User

enum class FriendshipStatus {
    NONE,
    REQUESTING,
    INCOMING,
    FRIENDS,
}

sealed class FriendProfileViewState {
    data object Loading : FriendProfileViewState()
    data class Error(val message: String) : FriendProfileViewState()
    data class Content(
        val user: User,
        val status: FriendshipStatus = FriendshipStatus.NONE,
        val isActionPending: Boolean = false,
        val actionError: String? = null,
    ) : FriendProfileViewState()
}
