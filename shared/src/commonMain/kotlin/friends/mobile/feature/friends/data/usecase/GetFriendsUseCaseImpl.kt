package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase

/**
 * Implementation of GetFriendsUseCase.
 *
 * Delegates to the repository.
 */
internal class GetFriendsUseCaseImpl(
    private val repository: FriendsRepository,
) : GetFriendsUseCase {

    override suspend fun invoke(): List<UserDto> =
        repository.getFriends()
}
