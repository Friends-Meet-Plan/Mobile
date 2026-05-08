package friends.mobile.feature.friends.data.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.friends.domain.repository.FriendsRepository
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase

/**
 * Implementation of SearchUserUseCase.
 *
 * Delegates to the repository and filters out the currently logged-in user
 * from the search results to prevent users from seeing themselves.
 */
internal class SearchUserUseCaseImpl(
    private val repository: FriendsRepository,
    private val tokenStorage: TokenStorage,
) : SearchUserUseCase {

    override suspend fun invoke(query: String): List<UserDto> {
        val results = repository.searchUsers(query)

        // Get current user ID from session
        val currentUserId = tokenStorage.getSession()?.user?.id

        // Filter out the current user if logged in
        return if (currentUserId != null) {
            results.filter { it.id != currentUserId }
        } else {
            results
        }
    }
}
