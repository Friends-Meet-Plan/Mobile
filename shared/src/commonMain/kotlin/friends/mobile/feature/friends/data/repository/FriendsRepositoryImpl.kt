package friends.mobile.feature.friends.data.repository

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.repository.FriendsRepository

/**
 * Implementation of FriendsRepository.
 *
 * Handles API calls and delegates to the API layer.
 * Future: add caching layer if needed.
 */
internal class FriendsRepositoryImpl(
    private val api: FriendsApi,
) : FriendsRepository {

    override suspend fun getFriends(): List<UserDto> =
        api.getFriends()

    override suspend fun getIncomingRequests(): List<UserDto> =
        api.getIncomingRequests()

    override suspend fun getOutgoingRequests(): List<UserDto> =
        api.getOutgoingRequests()

    override suspend fun sendFriendRequest(friendId: String) {
        api.sendFriendRequest(friendId)
    }

    override suspend fun acceptFriendRequest(requestId: String) {
        api.acceptFriendRequest(requestId)
    }

    override suspend fun rejectFriendRequest(requestId: String) {
        api.rejectFriendRequest(requestId)
    }

    override suspend fun cancelFriendRequest(requestId: String) {
        api.cancelFriendRequest(requestId)
    }

    override suspend fun searchUsers(query: String): List<UserDto> =
        api.searchUsers(query)
}
