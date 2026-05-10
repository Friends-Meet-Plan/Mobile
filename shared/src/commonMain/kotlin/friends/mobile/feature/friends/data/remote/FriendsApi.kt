package friends.mobile.feature.friends.data.remote

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.data.remote.dto.FriendIdBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

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
     * POST /friends/request
     * Request body: { "friend_id": "uuid" }
     * Response: empty 201
     */
    suspend fun sendFriendRequest(friendId: String) {
        client.post("/friends/request") {
            setBody(FriendIdBody(friendId = friendId))
        }
    }

    /**
     * Accept an incoming friend request.
     *
     * POST /friends/{userId}/accept
     * Response: empty 204
     */
    suspend fun acceptFriendRequest(userId: String) {
        client.post("/friends/$userId/accept")
    }

    /**
     * Reject an incoming friend request.
     *
     * POST /friends/{userId}/reject
     * Response: empty 204
     */
    suspend fun rejectFriendRequest(userId: String) {
        client.post("/friends/$userId/reject")
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

    /**
     * Remove a user from friends.
     *
     * DELETE /friends/{userId}/remove
     * Response: empty 204
     */
    suspend fun removeFriend(userId: String) {
        client.delete("/friends/$userId/remove")
    }

    /**
     * Get a user by ID.
     *
     * GET /users/{userId}
     * Response: { "id", "username", "avatar_url", "bio" }
     */
    suspend fun getUserById(userId: String): UserDto =
        client.get("/users/$userId").body()
}
