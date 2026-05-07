package friends.mobile.feature.friends.domain.repository

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Public interface for the friends repository.
 *
 * Handles fetching friends from the backend and any caching logic.
 */
interface FriendsRepository {

    /**
     * Fetch all available friends from the backend.
     *
     * May cache results if appropriate.
     * Throws exceptions: NetworkException.NetworkError, NetworkException.Unauthorized, etc.
     */
    suspend fun getFriends(): List<UserDto>
}
