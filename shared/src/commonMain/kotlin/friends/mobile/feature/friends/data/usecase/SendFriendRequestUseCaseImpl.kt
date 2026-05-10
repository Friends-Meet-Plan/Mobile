package friends.mobile.feature.friends.data.usecase

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase

/**
 * Implementation of SendFriendRequestUseCase.
 *
 * Delegates to the repository.
 */
internal class SendFriendRequestUseCaseImpl(
    private val repository: FriendsRepository
) : SendFriendRequestUseCase {
    override suspend fun invoke(friendId: String): ResultWrapper<Unit> {
        return repository.sendFriendRequest(friendId)
    }
}
