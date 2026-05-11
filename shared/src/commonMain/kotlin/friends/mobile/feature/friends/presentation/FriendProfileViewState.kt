package friends.mobile.feature.friends.presentation

import friends.mobile.feature.friends.domain.model.User

/**
 * Friend relationship status.
 *
 * NONE: not friends, no pending request
 * REQUESTING: outgoing friend request pending
 * INCOMING: incoming friend request pending
 * FRIENDS: already friends
 */
enum class FriendshipStatus {
    NONE,
    REQUESTING,
    INCOMING,
    FRIENDS,
}

/**
 * MVI view state for the friend profile screen.
 *
 * States:
 *   - Loading: fetching friend profile and relationship status
 *   - Error: something went wrong (network, auth, etc.)
 *   - Content: successfully loaded friend info and relationship status
 *
 * Properties:
 *   - user: the friend's user data
 *   - status: current friendship status with this user
 *   - isActionPending: true when a friend action (add/remove/accept/reject) is in progress
 *   - actionError: error message from a failed action
 */
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
