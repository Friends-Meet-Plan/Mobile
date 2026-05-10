package friends.mobile.feature.friends.data.repository

import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository

internal class FriendsRepositoryImpl(
    private val api: FriendsApi,
    private val userDtoMapper: UserDtoMapper,
) : FriendsRepository {

    override suspend fun getFriends(): List<User> =
        userDtoMapper.toDomain(api.getFriends())

    override suspend fun getIncomingRequests(): List<User> =
        userDtoMapper.toDomain(api.getIncomingRequests())

    override suspend fun getOutgoingRequests(): List<User> =
        userDtoMapper.toDomain(api.getOutgoingRequests())

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
        userDtoMapper.toDomain(api.searchUsers(query))
}
