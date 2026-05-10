package friends.mobile.feature.friends.domain.usecase

/**
 * Public interface for canceling a friend request.
 */
interface CancelFriendRequestUseCase {

    /**
     * Cancel an outgoing friend request by request ID.
     *
     * @param requestId The ID of the friend request to cancel
     * @throws NetworkException on failure
     */
    suspend operator fun invoke(requestId: String)
}
