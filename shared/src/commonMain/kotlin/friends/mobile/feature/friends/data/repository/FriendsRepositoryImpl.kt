package friends.mobile.feature.friends.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.data.remote.FriendsApi
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository

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

    override suspend fun sendFriendRequest(friendId: String): ResultWrapper<Unit> = safeApiCall {
        api.sendFriendRequest(friendId)
    }

    override suspend fun acceptFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.acceptFriendRequest(requestId)
    }

    override suspend fun rejectFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.rejectFriendRequest(requestId)
    }

    override suspend fun cancelFriendRequest(requestId: String): ResultWrapper<Unit> = safeApiCall {
        api.cancelFriendRequest(requestId)
    }

    override suspend fun searchUsers(query: String): ResultWrapper<List<User>> = safeApiCall {
        userDtoMapper.toDomain(api.searchUsers(query))
    }
}
