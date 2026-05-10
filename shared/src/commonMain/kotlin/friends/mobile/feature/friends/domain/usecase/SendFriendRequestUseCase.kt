package friends.mobile.feature.friends.domain.usecase

/**
 * Public interface for sending a friend request.
 */
interface SendFriendRequestUseCase {

    /**
     * Send a friend request to a user by ID.
     *
     * @param friendId The ID of the user to send the request to
     * @throws NetworkException on failure
     */
    suspend operator fun invoke(friendId: String)
}
