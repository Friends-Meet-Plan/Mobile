package friends.mobile.feature.friends.presentation

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FriendsViewModel :
    BaseViewModel<FriendsViewState, FriendsAction, FriendsEvent>(
        initState = FriendsViewState.Content(),
    ),
    KoinComponent {

    private val getFriendsUseCase: GetFriendsUseCase by inject()
    private val sendFriendRequestUseCase: SendFriendRequestUseCase by inject()
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase by inject()
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase by inject()
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase by inject()
    private val searchUserUseCase: SearchUserUseCase by inject()

    override fun obtainEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.ScreenOpened -> onScreenOpen()
            is FriendsEvent.OnSendRequest -> onSendRequest(event.friendId)
            is FriendsEvent.OnAcceptRequest -> onAcceptRequest(event.requestId)
            is FriendsEvent.OnRejectRequest -> onRejectRequest(event.requestId)
            is FriendsEvent.OnCancelRequest -> onCancelRequest(event.requestId)
            is FriendsEvent.OnSearchUsers -> onSearchUsers(event.query)
        }
    }

    private fun onScreenOpen() {
        viewModelScope.launch {
            viewState = FriendsViewState.Loading

            try {
                val friends = getFriendsUseCase()
                viewState = FriendsViewState.Content(friends = friends)
            } catch (_: NetworkException.NetworkError) {
                viewState = FriendsViewState.Error("Network error, check your connection")
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = FriendsViewState.Error("Something went wrong")
            } catch (_: NetworkException) {
                viewState = FriendsViewState.Error("Failed to load friends")
            }
        }
    }

    private fun onSendRequest(friendId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)

            try {
                sendFriendRequestUseCase(friendId)
                viewState = currentState.copy(isRequestPending = false, requestError = null)
            } catch (_: NetworkException.NetworkError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Network error, check your connection"
                )
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Something went wrong"
                )
            } catch (_: NetworkException) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Failed to send friend request"
                )
            }
        }
    }

    private fun onAcceptRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)

            try {
                acceptFriendRequestUseCase(requestId)
                viewState = currentState.copy(isRequestPending = false, requestError = null)
            } catch (_: NetworkException.NetworkError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Network error, check your connection"
                )
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Something went wrong"
                )
            } catch (_: NetworkException) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Failed to accept friend request"
                )
            }
        }
    }

    private fun onRejectRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)

            try {
                rejectFriendRequestUseCase(requestId)
                viewState = currentState.copy(isRequestPending = false, requestError = null)
            } catch (_: NetworkException.NetworkError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Network error, check your connection"
                )
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Something went wrong"
                )
            } catch (_: NetworkException) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Failed to reject friend request"
                )
            }
        }
    }

    private fun onCancelRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)

            try {
                cancelFriendRequestUseCase(requestId)
                viewState = currentState.copy(isRequestPending = false, requestError = null)
            } catch (_: NetworkException.NetworkError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Network error, check your connection"
                )
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Something went wrong"
                )
            } catch (_: NetworkException) {
                viewState = currentState.copy(
                    isRequestPending = false,
                    requestError = "Failed to cancel friend request"
                )
            }
        }
    }

    private fun onSearchUsers(query: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isSearching = true, requestError = null)

            try {
                val results = searchUserUseCase(query)
                viewState = currentState.copy(
                    searchResults = results,
                    isSearching = false,
                    requestError = null
                )
            } catch (_: NetworkException.NetworkError) {
                viewState = currentState.copy(
                    isSearching = false,
                    requestError = "Network error, check your connection"
                )
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = currentState.copy(
                    isSearching = false,
                    requestError = "Something went wrong"
                )
            } catch (_: NetworkException) {
                viewState = currentState.copy(
                    isSearching = false,
                    requestError = "Failed to search users"
                )
            }
        }
    }
}
