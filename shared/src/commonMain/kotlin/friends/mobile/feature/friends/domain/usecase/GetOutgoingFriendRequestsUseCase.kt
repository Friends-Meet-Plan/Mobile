package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Public interface for the get outgoing friend requests use case.
 */
interface GetOutgoingFriendRequestsUseCase {

    /**
     * Fetch outgoing friend requests.
     */
    suspend operator fun invoke(): List<UserDto>
}
