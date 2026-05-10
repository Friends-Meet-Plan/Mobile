package friends.mobile.feature.friends.domain.repository

import friends.mobile.feature.friends.domain.model.User

/**
 * Public interface for the friends repository.
 *
 * Handles fetching friends and managing friend requests.
 */
interface FriendsRepository {

    /**
     * Fetch all available friends from the backend.
     *
     * May cache results if appropriate.
     * Throws exceptions: NetworkException.NetworkError, NetworkException.Unauthorized, etc.
     */
    suspend fun getFriends(): List<User>

    /**
     * Fetch incoming friend requests.
     *
     * @return List of users who have sent friend requests to the current user
     * @throws NetworkException on failure
     */
    suspend fun getIncomingRequests(): List<User>

    /**
     * Fetch outgoing friend requests.
     *
     * @return List of users to whom the current user has sent friend requests
     * @throws NetworkException on failure
     */
    suspend fun getOutgoingRequests(): List<User>

    /**
     * Send a friend request to a user.
     *
     * @param friendId The ID of the user to send the request to
     * @throws NetworkException on failure
     */
    suspend fun sendFriendRequest(friendId: String)

    /**
     * Accept an incoming friend request.
     *
     * @param requestId The ID of the friend request to accept
     * @throws NetworkException on failure
     */
    suspend fun acceptFriendRequest(requestId: String)

    /**
     * Reject an incoming friend request.
     *
     * @param requestId The ID of the friend request to reject
     * @throws NetworkException on failure
     */
    suspend fun rejectFriendRequest(requestId: String)

    /**
     * Cancel an outgoing friend request.
     *
     * @param requestId The ID of the friend request to cancel
     * @throws NetworkException on failure
     */
    suspend fun cancelFriendRequest(requestId: String)

    /**
     * Search for users by name/username.
     *
     * @param query The search query string
     * @return List of matching users, empty if no matches found
     * @throws NetworkException on failure
     */
    suspend fun searchUsers(query: String): List<User>
}
