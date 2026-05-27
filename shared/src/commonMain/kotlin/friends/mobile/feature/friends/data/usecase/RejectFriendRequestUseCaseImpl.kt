package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase

internal class RejectFriendRequestUseCaseImpl(
    private val repository: FriendsRepository
) : RejectFriendRequestUseCase {
    override suspend fun invoke(requestId: String): ResultWrapper<Unit> {
        return repository.rejectFriendRequest(requestId)
    }
}
