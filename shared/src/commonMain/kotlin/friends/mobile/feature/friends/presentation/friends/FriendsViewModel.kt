package friends.mobile.feature.friends.presentation.friends

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getIncomingFriendRequestsUseCase: GetIncomingFriendRequestsUseCase,
    private val getOutgoingFriendRequestsUseCase: GetOutgoingFriendRequestsUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val searchUserUseCase: SearchUserUseCase
) : BaseViewModel<FriendsViewState, FriendsAction, FriendsEvent>(
    initState = FriendsViewState.Loading,
) {

    private var searchJob: Job? = null
    private var dataJob: Job? = null

    init {
        obtainEvent(FriendsEvent.ScreenOpened)
    }

    override fun obtainEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.ScreenOpened -> loadData(showLoading = true)
            is FriendsEvent.ReloadCurrentTab -> loadData(showLoading = false)
            is FriendsEvent.OnTabSelected -> onTabSelected(event.tab)
            is FriendsEvent.OnSearchUsers -> onSearchChanged(event.query)
            is FriendsEvent.OnUserClick -> viewAction = FriendsAction.NavigateToFriendProfile(event.userId)
            is FriendsEvent.OnSendRequest -> performAction { sendFriendRequestUseCase(event.friendId) }
            is FriendsEvent.OnAcceptRequest -> performAction { acceptFriendRequestUseCase(event.requestId) }
            is FriendsEvent.OnRejectRequest -> performAction { rejectFriendRequestUseCase(event.requestId) }
            is FriendsEvent.OnCancelRequest -> performAction { cancelFriendRequestUseCase(event.requestId) }
        }
    }

    private fun loadData(showLoading: Boolean) {
        val currentState = viewState as? FriendsViewState.Content
        val tab = currentState?.currentTab ?: RequestTab.FRIENDS
        
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            if (showLoading || viewState !is FriendsViewState.Content) {
                viewState = FriendsViewState.Loading
            }

            val result = when (tab) {
                RequestTab.FRIENDS -> getFriendsUseCase()
                RequestTab.INCOMING -> getIncomingFriendRequestsUseCase()
                RequestTab.OUTGOING -> getOutgoingFriendRequestsUseCase()
            }

            when (result) {
                is ResultWrapper.Success -> {
                    val baseContent = (viewState as? FriendsViewState.Content) ?: FriendsViewState.Content(currentTab = tab)
                    viewState = when (tab) {
                        RequestTab.FRIENDS -> baseContent.copy(friendsList = result.data, currentTab = tab)
                        RequestTab.INCOMING -> baseContent.copy(incomingRequests = result.data, currentTab = tab)
                        RequestTab.OUTGOING -> baseContent.copy(outgoingRequests = result.data, currentTab = tab)
                    }
                }
                is ResultWrapper.Error -> {
                    if (viewState !is FriendsViewState.Content) {
                        val userError = mapApiErrorToUserFriendly(result.error)
                        viewState = FriendsViewState.Error(getErrorMessage(userError))
                    } else {
                        viewAction = FriendsAction.ShowError(getErrorMessage(mapApiErrorToUserFriendly(result.error)))
                    }
                }
            }
        }
    }

    private fun onTabSelected(tab: RequestTab) {
        updateContent { it.copy(currentTab = tab, searchText = "", searchResults = null) }
        loadData(showLoading = true)
    }

    private fun onSearchChanged(query: String) {
        updateContent { it.copy(searchText = query) }
        
        searchJob?.cancel()
        if (query.isBlank()) {
            updateContent { it.copy(searchResults = null, isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce search
            updateContent { it.copy(isSearching = true) }
            when (val result = searchUserUseCase(query)) {
                is ResultWrapper.Success -> {
                    updateContent { it.copy(searchResults = result.data, isSearching = false) }
                }
                is ResultWrapper.Error -> {
                    updateContent { it.copy(isSearching = false) }
                    viewAction = FriendsAction.ShowError(getErrorMessage(mapApiErrorToUserFriendly(result.error)))
                }
            }
        }
    }

    private fun performAction(action: suspend () -> ResultWrapper<Unit>) {
        val currentState = viewState as? FriendsViewState.Content ?: return
        viewModelScope.launch {
            updateContent { it.copy(isActionPending = true) }
            when (val result = action()) {
                is ResultWrapper.Success -> {
                    updateContent { it.copy(isActionPending = false) }
                    loadData(showLoading = false)
                }
                is ResultWrapper.Error -> {
                    updateContent { it.copy(isActionPending = false) }
                    viewAction = FriendsAction.ShowError(getErrorMessage(mapApiErrorToUserFriendly(result.error)))
                }
            }
        }
    }

    private fun updateContent(transform: (FriendsViewState.Content) -> FriendsViewState.Content) {
        (viewState as? FriendsViewState.Content)?.let {
            viewState = transform(it)
        }
    }
}
