package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetOutgoingFriendRequestsUseCase

/**
 * Implementation of GetOutgoingFriendRequestsUseCase.
 *
 * Delegates to the repository.
 */
internal class GetOutgoingFriendRequestsUseCaseImpl(
    private val repository: FriendsRepository,
) : GetOutgoingFriendRequestsUseCase {

    override suspend fun invoke(): List<User> =
        repository.getOutgoingRequests()
}
