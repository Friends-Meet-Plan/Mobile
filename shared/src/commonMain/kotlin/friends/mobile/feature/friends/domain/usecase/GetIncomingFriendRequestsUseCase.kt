package friends.mobile.feature.friends.domain.usecase

import friends.mobile.feature.friends.domain.model.User

/**
 * Public interface for the get incoming friend requests use case.
 */
interface GetIncomingFriendRequestsUseCase {

    /**
     * Fetch incoming friend requests.
     */
    suspend operator fun invoke(): List<User>
}
