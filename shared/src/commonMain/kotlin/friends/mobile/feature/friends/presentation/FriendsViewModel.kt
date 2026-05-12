package friends.mobile.feature.friends.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.AcceptFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.CancelFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import friends.mobile.feature.friends.domain.usecase.GetIncomingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.GetOutgoingFriendRequestsUseCase
import friends.mobile.feature.friends.domain.usecase.RejectFriendRequestUseCase
import friends.mobile.feature.friends.domain.usecase.SearchUserUseCase
import friends.mobile.feature.friends.domain.usecase.SendFriendRequestUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FriendsViewModel :
    BaseViewModel<FriendsViewState, Unit, FriendsEvent>(
        initState = FriendsViewState.Content(),
    ),
    KoinComponent {

    private val getFriendsUseCase: GetFriendsUseCase by inject()
    private val getIncomingFriendRequestsUseCase: GetIncomingFriendRequestsUseCase by inject()
    private val getOutgoingFriendRequestsUseCase: GetOutgoingFriendRequestsUseCase by inject()
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
            is FriendsEvent.OnTabSelected -> onTabSelected(event.tab)
            is FriendsEvent.ReloadCurrentTab -> onReloadCurrentTab()
        }
    }

    private fun onScreenOpen() {
        viewModelScope.launch {
            viewState = FriendsViewState.Loading
            when (val result = getFriendsUseCase()) {
                is ResultWrapper.Success -> {
                    viewState = FriendsViewState.Content(friendsList = result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = FriendsViewState.Error(getErrorMessage(userError))
                }
            }
        }
    }

    private fun onTabSelected(tab: RequestTab) {
        val currentState = viewState as? FriendsViewState.Content ?: return
        if (currentState.currentTab == tab) return

        viewModelScope.launch {
            viewState = currentState.copy(currentTab = tab)
            val result = when (tab) {
                RequestTab.FRIENDS -> getFriendsUseCase()
                RequestTab.INCOMING -> getIncomingFriendRequestsUseCase()
                RequestTab.OUTGOING -> getOutgoingFriendRequestsUseCase()
            }

            when (result) {
                is ResultWrapper.Success -> {
                    viewState = when (tab) {
                        RequestTab.FRIENDS -> currentState.copy(
                            currentTab = tab,
                            friendsList = result.data,
                            searchResults = null
                        )
                        RequestTab.INCOMING -> currentState.copy(
                            currentTab = tab,
                            incomingRequests = result.data,
                            searchResults = null
                        )
                        RequestTab.OUTGOING -> currentState.copy(
                            currentTab = tab,
                            outgoingRequests = result.data,
                            searchResults = null
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onReloadCurrentTab() {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            val result = when (currentState.currentTab) {
                RequestTab.FRIENDS -> getFriendsUseCase()
                RequestTab.INCOMING -> getIncomingFriendRequestsUseCase()
                RequestTab.OUTGOING -> getOutgoingFriendRequestsUseCase()
            }

            when (result) {
                is ResultWrapper.Success -> {
                    viewState = when (currentState.currentTab) {
                        RequestTab.FRIENDS -> currentState.copy(friendsList = result.data)
                        RequestTab.INCOMING -> currentState.copy(incomingRequests = result.data)
                        RequestTab.OUTGOING -> currentState.copy(outgoingRequests = result.data)
                    }
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onSendRequest(friendId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)
            when (val result = sendFriendRequestUseCase(friendId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isRequestPending = false, requestError = null)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(isRequestPending = false, requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onAcceptRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)
            when (val result = acceptFriendRequestUseCase(requestId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isRequestPending = false, requestError = null)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(isRequestPending = false, requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onRejectRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)
            when (val result = rejectFriendRequestUseCase(requestId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isRequestPending = false, requestError = null)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(isRequestPending = false, requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onCancelRequest(requestId: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isRequestPending = true, requestError = null)
            when (val result = cancelFriendRequestUseCase(requestId)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isRequestPending = false, requestError = null)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(isRequestPending = false, requestError = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onSearchUsers(query: String) {
        val currentState = viewState as? FriendsViewState.Content ?: return

        viewModelScope.launch {
            viewState = currentState.copy(isSearching = true, requestError = null)
            when (val result = searchUserUseCase(query)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        searchResults = result.data,
                        isSearching = false,
                        requestError = null
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(isSearching = false, requestError = getErrorMessage(userError))
                }
            }
        }
    }
}
