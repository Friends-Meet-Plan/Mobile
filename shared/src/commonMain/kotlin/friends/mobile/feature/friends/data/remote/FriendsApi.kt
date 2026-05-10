package friends.mobile.feature.friends.data.remote

import friends.mobile.feature.auth.data.remote.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

/**
 * HTTP API wrapper for the friends feature.
 *
 * Uses the authenticated HttpClient (named "auth") to make requests.
 * This client handles token refresh, 401 retry, and proactive token refresh.
 */
internal class FriendsApi(
    private val client: HttpClient,
) {

    /**
     * Fetch all available friends from the backend.
     *
     * GET /friends
     * Response: [ { "id", "username", "avatar_url", "bio" }, ... ]
     */
    suspend fun getFriends(): List<UserDto> =
        client.get("/friends").body()

    /**
     * Fetch incoming friend requests.
     *
     * GET /friends/incoming
     * Response: [ { "id", "username", "avatar_url", "bio" }, ... ]
     */
    suspend fun getIncomingRequests(): List<UserDto> =
        client.get("/friends/incoming").body()

    /**
     * Fetch outgoing friend requests.
     *
     * GET /friends/outgoing
     * Response: [ { "id", "username", "avatar_url", "bio" }, ... ]
     */
    suspend fun getOutgoingRequests(): List<UserDto> =
        client.get("/friends/outgoing").body()

    /**
     * Send a friend request to a user.
     *
     * POST /friend-requests/{friendId}
     * Response: empty 201
     */
    suspend fun sendFriendRequest(friendId: String) {
        client.post("/friend-requests/$friendId")
    }

    /**
     * Accept an incoming friend request.
     *
     * POST /friend-requests/{requestId}/accept
     * Response: empty 200
     */
    suspend fun acceptFriendRequest(requestId: String) {
        client.post("/friend-requests/$requestId/accept")
    }

    /**
     * Reject an incoming friend request.
     *
     * POST /friend-requests/{requestId}/reject
     * Response: empty 204
     */
    suspend fun rejectFriendRequest(requestId: String) {
        client.post("/friend-requests/$requestId/reject")
    }

    /**
     * Cancel an outgoing friend request.
     *
     * DELETE /friend-requests/{requestId}
     * Response: empty 204
     */
    suspend fun cancelFriendRequest(requestId: String) {
        client.delete("/friend-requests/$requestId")
    }

    /**
     * Search for users by name/username.
     *
     * GET /users/search?username={username}
     * Response: [ { "id", "username", "avatar_url", "bio" }, ... ]
     */
    suspend fun searchUsers(query: String): List<UserDto> =
        client.get("/users/search") {
            parameter("username", query)
        }.body()
}
