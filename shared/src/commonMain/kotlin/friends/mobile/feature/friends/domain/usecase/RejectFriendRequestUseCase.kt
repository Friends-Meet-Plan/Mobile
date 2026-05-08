package friends.mobile.feature.friends.domain.usecase

/**
 * Public interface for rejecting a friend request.
 */
interface RejectFriendRequestUseCase {

    /**
     * Reject an incoming friend request by request ID.
     *
     * @param requestId The ID of the friend request to reject
     * @throws NetworkException on failure
     */
    suspend operator fun invoke(requestId: String)
}
