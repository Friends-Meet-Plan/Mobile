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

internal class FriendsApi(
    private val client: HttpClient,
) {
    suspend fun getFriends(): List<UserDto> =
        client.get("/friends").body()

    suspend fun getIncomingRequests(): List<UserDto> =
        client.get("/friends/incoming").body()

    suspend fun getOutgoingRequests(): List<UserDto> =
        client.get("/friends/outgoing").body()

    suspend fun sendFriendRequest(friendId: String) {
        client.post("/friends/request") {
            setBody(FriendIdBody(friendId = friendId))
        }
    }

    suspend fun acceptFriendRequest(userId: String) {
        client.post("/friends/$userId/accept")
    }

    suspend fun rejectFriendRequest(userId: String) {
        client.post("/friends/$userId/reject")
    }

    suspend fun cancelFriendRequest(requestId: String) {
        client.delete("/friend-requests/$requestId")
    }

    suspend fun searchUsers(query: String): List<UserDto> =
        client.get("/users/search") {
            parameter("username", query)
        }.body()

    suspend fun removeFriend(userId: String) {
        client.delete("/friends/$userId/remove")
    }

    suspend fun getUserById(userId: String): UserDto =
        client.get("/users/$userId").body()
}
