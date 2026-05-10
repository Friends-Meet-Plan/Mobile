package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase

/**
 * Implementation of AcceptFriendRequestUseCase.
 *
 * Delegates to the repository.
 */
internal class AcceptFriendRequestUseCaseImpl(
    private val repository: FriendsRepository,
) : AcceptFriendRequestUseCase {

    override suspend fun invoke(requestId: String) {
        repository.acceptFriendRequest(requestId)
    }
}
