package friends.mobile.feature.friends.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.FriendStatus
import friends.mobile.feature.friends.domain.model.User

interface FriendsRepository {
    suspend fun getFriends(): ResultWrapper<List<User>>
    suspend fun getIncomingRequests(): ResultWrapper<List<User>>
    suspend fun getOutgoingRequests(): ResultWrapper<List<User>>
    suspend fun getFriendStatus(userId: String): FriendStatus
    suspend fun sendFriendRequest(friendId: String): ResultWrapper<Unit>
    suspend fun acceptFriendRequest(requestId: String): ResultWrapper<Unit>
    suspend fun rejectFriendRequest(requestId: String): ResultWrapper<Unit>
    suspend fun removeFriend(friendId: String): ResultWrapper<Unit>
    suspend fun cancelFriendRequest(requestId: String): ResultWrapper<Unit>
    suspend fun searchUsers(query: String): ResultWrapper<List<User>>
}
