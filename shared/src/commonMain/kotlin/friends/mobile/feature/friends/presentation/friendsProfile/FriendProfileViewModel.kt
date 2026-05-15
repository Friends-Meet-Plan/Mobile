package friends.mobile.feature.friends.presentation.friendsProfile

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendStatusUseCase
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.RemoveFriendUseCase
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase
import kotlinx.coroutines.launch

class FriendProfileViewModel(
    private val getFriendStatusUseCase: GetFriendStatusUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
) : BaseViewModel<FriendProfileViewState, FriendProfileAction, FriendProfileEvent>(
    initState = FriendProfileViewState.Loading,
) {

    override fun obtainEvent(event: FriendProfileEvent) {
        when (event) {
            is FriendProfileEvent.ScreenOpened -> loadFriendProfile(event.userId)
            is FriendProfileEvent.OnSendRequest -> sendFriendRequest(event.userId)
            is FriendProfileEvent.OnAcceptRequest -> acceptFriendRequest(event.userId)
            is FriendProfileEvent.OnRejectRequest -> rejectFriendRequest(event.userId)
            is FriendProfileEvent.OnRemoveFriend -> removeFriend(event.userId)
        }
    }

    private fun loadFriendProfile(userId: String) {
        viewModelScope.launch {
            viewState = FriendProfileViewState.Loading
            when (val result = getFriendStatusUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = FriendProfileViewState.Content(
                        user = result.data.user,
                        status = result.data.status,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = FriendProfileViewState.Error(getErrorMessage(userError))
                }
            }
        }
    }

    private fun sendFriendRequest(userId: String) {
        performAction(userId) { sendFriendRequestUseCase(userId) }
    }

    private fun acceptFriendRequest(userId: String) {
        performAction(userId) { acceptFriendRequestUseCase(userId) }
    }

    private fun rejectFriendRequest(userId: String) {
        performAction(userId) { rejectFriendRequestUseCase(userId) }
    }

    private fun removeFriend(userId: String) {
        performAction(userId) { removeFriendUseCase(userId) }
    }

    private fun performAction(userId: String, action: suspend () -> ResultWrapper<Unit>) {
        val currentState = viewState as? FriendProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isActionPending = true)
            when (val result = action()) {
                is ResultWrapper.Success -> {
                    // Re-load status to get the updated UI state
                    loadFriendProfile(userId)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isActionPending = false,
                    )
                }
            }
        }
    }
}
