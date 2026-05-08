package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Public interface for the get incoming friend requests use case.
 */
interface GetIncomingFriendRequestsUseCase {

    /**
     * Fetch incoming friend requests.
     *
     * @param page the page number (default: 1)
     */
    suspend operator fun invoke(page: Int = 1): List<UserDto>
}
