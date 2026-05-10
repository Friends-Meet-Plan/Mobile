package friends.mobile.feature.friends.domain.usecase

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.model.FriendStatus
import friends.mobile.feature.friends.domain.repository.FriendsRepository

class GetFriendStatusUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(userId: String): ResultWrapper<FriendStatus> = try {
        ResultWrapper.Success(repository.getFriendStatus(userId))
    } catch (e: Throwable) {
        ResultWrapper.Error(ApiError(code = 500, message = e.message ?: "Unknown error"))
    }
}
