package friends.mobile.feature.friends.presentation

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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FriendProfileViewModel :
    BaseViewModel<FriendProfileViewState, FriendProfileAction, FriendProfileEvent>(
        initState = FriendProfileViewState.Loading,
    ),
    KoinComponent {

    private val getFriendStatusUseCase: GetFriendStatusUseCase by inject()
    private val sendFriendRequestUseCase: SendFriendRequestUseCase by inject()
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase by inject()
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase by inject()
    private val removeFriendUseCase: RemoveFriendUseCase by inject()

    private var currentUserId: String = ""

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
        currentUserId = userId
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
        val currentState = viewState as? FriendProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isActionPending = true)
            when (val result = sendFriendRequestUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        isActionPending = false,
                        status = FriendshipStatus.REQUESTING,
                        actionError = null,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isActionPending = false,
                        actionError = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun acceptFriendRequest(userId: String) {
        val currentState = viewState as? FriendProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isActionPending = true)
            when (val result = acceptFriendRequestUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        isActionPending = false,
                        status = FriendshipStatus.FRIENDS,
                        actionError = null,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isActionPending = false,
                        actionError = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun rejectFriendRequest(userId: String) {
        val currentState = viewState as? FriendProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isActionPending = true)
            when (val result = rejectFriendRequestUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        isActionPending = false,
                        status = FriendshipStatus.NONE,
                        actionError = null,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isActionPending = false,
                        actionError = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun removeFriend(userId: String) {
        val currentState = viewState as? FriendProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isActionPending = true)
            when (val result = removeFriendUseCase(userId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        isActionPending = false,
                        status = FriendshipStatus.NONE,
                        actionError = null,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isActionPending = false,
                        actionError = getErrorMessage(userError),
                    )
                }
            }
        }
    }
}
