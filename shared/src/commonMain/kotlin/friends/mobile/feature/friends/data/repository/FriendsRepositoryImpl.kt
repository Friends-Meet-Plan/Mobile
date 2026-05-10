package friends.mobile.feature.friends.data.repository

import friends.mobile.feature.friends.data.mapper.toDomain
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository

/**
 * Implementation of FriendsRepository.
 *
 * Handles API calls, maps DTOs to domain models.
 * Future: add caching layer if needed.
 */
internal class FriendsRepositoryImpl(
    private val api: FriendsApi,
) : FriendsRepository {

    override suspend fun getFriends(): List<User> =
        api.getFriends().toDomain()

    override suspend fun getIncomingRequests(): List<User> =
        api.getIncomingRequests().toDomain()

    override suspend fun getOutgoingRequests(): List<User> =
        api.getOutgoingRequests().toDomain()

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

    override suspend fun searchUsers(query: String): List<User> =
        api.searchUsers(query).toDomain()
}
