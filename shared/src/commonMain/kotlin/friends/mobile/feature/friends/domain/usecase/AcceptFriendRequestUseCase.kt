package friends.mobile.feature.friends.domain.usecase

/**
 * Public interface for accepting a friend request.
 */
interface AcceptFriendRequestUseCase {

    /**
     * Accept an incoming friend request by request ID.
     *
     * @param requestId The ID of the friend request to accept
     * @throws NetworkException on failure
     */
    suspend operator fun invoke(requestId: String)
}
