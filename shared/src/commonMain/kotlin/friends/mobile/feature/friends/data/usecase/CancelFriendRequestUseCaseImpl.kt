package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase

/**
 * Implementation of CancelFriendRequestUseCase.
 *
 * Delegates to the repository.
 */
internal class CancelFriendRequestUseCaseImpl(
    private val repository: FriendsRepository,
) : CancelFriendRequestUseCase {

    override suspend fun invoke(requestId: String): ResultWrapper<Unit> {
        return repository.cancelFriendRequest(requestId)
    }
}
