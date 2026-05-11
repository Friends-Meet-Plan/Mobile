package friends.mobile.feature.friends.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.model.FriendStatus
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.presentation.FriendshipStatus

internal class FriendsRepositoryImpl(
    private val api: FriendsApi,
    private val userDtoMapper: UserDtoMapper,
) : FriendsRepository {

    override suspend fun getFriends(): ResultWrapper<List<User>> = safeApiCall {
        userDtoMapper.toDomain(api.getFriends())
    }

    override suspend fun getIncomingRequests(): ResultWrapper<List<User>> = safeApiCall {
        userDtoMapper.toDomain(api.getIncomingRequests())
    }

    override suspend fun getOutgoingRequests(): ResultWrapper<List<User>> = safeApiCall {
        userDtoMapper.toDomain(api.getOutgoingRequests())
    }

    override suspend fun getFriendStatus(userId: String): ResultWrapper<FriendStatus> = safeApiCall {
        val user = userDtoMapper.toDomain(api.getUserById(userId))

        val friends = userDtoMapper.toDomain(api.getFriends())
        val incoming = userDtoMapper.toDomain(api.getIncomingRequests())
        val outgoing = userDtoMapper.toDomain(api.getOutgoingRequests())

        val status = when {
            friends.any { it.id == userId } -> FriendshipStatus.FRIENDS
            incoming.any { it.id == userId } -> FriendshipStatus.INCOMING
            outgoing.any { it.id == userId } -> FriendshipStatus.REQUESTING
            else -> FriendshipStatus.NONE
        }

        FriendStatus(user = user, status = status)
    }

    override suspend fun sendFriendRequest(friendId: String): ResultWrapper<Unit> = safeApiCall {
        api.sendFriendRequest(friendId)
    }

    override suspend fun acceptFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.acceptFriendRequest(requestId)
    }

    override suspend fun rejectFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.rejectFriendRequest(requestId)
    }

    override suspend fun removeFriend(friendId: String): ResultWrapper<Unit> = safeApiCall {
        api.removeFriend(friendId)
    }

    override suspend fun cancelFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.cancelFriendRequest(requestId)
    }

    override suspend fun searchUsers(query: String): ResultWrapper<List<User>> = safeApiCall {
        userDtoMapper.toDomain(api.searchUsers(query))
    }
}
