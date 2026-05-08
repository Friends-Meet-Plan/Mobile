package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetIncomingFriendRequestsUseCase

/**
 * Implementation of GetIncomingFriendRequestsUseCase.
 *
 * Delegates to the repository.
 */
internal class GetIncomingFriendRequestsUseCaseImpl(
    private val repository: FriendsRepository,
) : GetIncomingFriendRequestsUseCase {

    override suspend fun invoke(): List<UserDto> =
        repository.getIncomingRequests()
}
