package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Public interface for the search users use case.
 */
interface SearchUserUseCase {

    /**
     * Search for users by name/username.
     *
     * @param query The search query string
     * @return List of matching users, empty if no matches found
     */
    suspend operator fun invoke(query: String): List<UserDto>
}
