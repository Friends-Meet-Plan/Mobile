package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase

/**
 * Implementation of SearchUserUseCase.
 *
 * Delegates to the repository.
 */
internal class SearchUserUseCaseImpl(
    private val repository: FriendsRepository,
) : SearchUserUseCase {

    override suspend fun invoke(query: String): List<UserDto> =
        repository.searchUsers(query)
}
