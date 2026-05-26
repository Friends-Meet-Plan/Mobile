package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase

internal class CancelFriendRequestUseCaseImpl(
    private val repository: FriendsRepository,
) : CancelFriendRequestUseCase {

    override suspend fun invoke(requestId: String): ResultWrapper<Unit> {
        return repository.cancelFriendRequest(requestId)
    }
}
