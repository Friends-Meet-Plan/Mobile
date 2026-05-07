package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Public interface for the get friends use case.
 */
interface GetFriendsUseCase {

    /**
     * Fetch all available friends.
     */
    suspend operator fun invoke(): List<UserDto>
}
