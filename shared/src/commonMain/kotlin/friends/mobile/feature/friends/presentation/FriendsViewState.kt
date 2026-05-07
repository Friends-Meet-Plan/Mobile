package friends.mobile.feature.friends.presentation

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.model.PaginationInfo

/**
 * MVI view state for the friends screen.
 *
 * States:
 *   - Loading: fetching friends from the backend
 *   - Error: something went wrong (network, auth, etc.)
 *   - Content: successfully loaded friends list (may be empty)
 */
sealed class FriendsViewState {
    data object Loading : FriendsViewState()
    data class Error(val message: String) : FriendsViewState()
    data class Content(val friends: List<UserDto> = emptyList()) : FriendsViewState()
}
