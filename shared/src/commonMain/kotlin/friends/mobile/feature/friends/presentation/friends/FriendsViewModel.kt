package friends.mobile.feature.friends.presentation.friends

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FriendsViewModel : BaseViewModel<FriendsViewState, FriendsAction, FriendsEvent>(
    initState = FriendsViewState.Loading,
), KoinComponent {

    private val getFriendsUseCase: GetFriendsUseCase by inject()
    private val getIncomingFriendRequestsUseCase: GetIncomingFriendRequestsUseCase by inject()
    private val getOutgoingFriendRequestsUseCase: GetOutgoingFriendRequestsUseCase by inject()
    private val sendFriendRequestUseCase: SendFriendRequestUseCase by inject()
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase by inject()
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase by inject()
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase by inject()
    private val searchUserUseCase: SearchUserUseCase by inject()

    private val searchQueryFlow = MutableSharedFlow<String>()
    private val loadTriggerFlow = MutableSharedFlow<Boolean>()

    init {
        setupSearchFlow()
        setupLoadDataFlow()
        obtainEvent(FriendsEvent.ScreenOpened)
    }

    override fun obtainEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.ScreenOpened -> {
                viewModelScope.launch { loadTriggerFlow.emit(true) }
            }
            is FriendsEvent.ReloadCurrentTab -> {
                viewModelScope.launch { loadTriggerFlow.emit(false) }
            }
            is FriendsEvent.OnTabSelected -> {
                updateContent { it.copy(currentTab = event.tab, searchText = "", searchResults = null) }
                viewModelScope.launch { loadTriggerFlow.emit(false) }
            }
            is FriendsEvent.OnSearchUsers -> {
                updateContent { it.copy(searchText = event.query) }
                viewModelScope.launch { searchQueryFlow.emit(event.query) }
            }
            is FriendsEvent.OnUserClick -> {
                viewAction = FriendsAction.NavigateToFriendProfile(event.userId)
            }
            is FriendsEvent.OnSendRequest -> performAction { sendFriendRequestUseCase(event.friendId) }
            is FriendsEvent.OnAcceptRequest -> performAction { acceptFriendRequestUseCase(event.requestId) }
            is FriendsEvent.OnRejectRequest -> performAction { rejectFriendRequestUseCase(event.requestId) }
            is FriendsEvent.OnCancelRequest -> performAction { cancelFriendRequestUseCase(event.requestId) }
        }
    }

    private fun setupSearchFlow() {
        searchQueryFlow
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    updateContent { it.copy(searchResults = null, isSearching = false) }
                    return@onEach
                }
                
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
            .launchIn(viewModelScope)
    }

    private fun setupLoadDataFlow() {
        loadTriggerFlow
            .transformLatest { isInitial ->
                val currentState = viewState as? FriendsViewState.Content
                val tab = currentState?.currentTab ?: RequestTab.FRIENDS

                if (isInitial || viewState !is FriendsViewState.Content) {
                    viewState = FriendsViewState.Loading
                } else {
                    updateContent { it.copy(isTabLoading = true) }
                }

                val result = when (tab) {
                    RequestTab.FRIENDS -> getFriendsUseCase()
                    RequestTab.INCOMING -> getIncomingFriendRequestsUseCase()
                    RequestTab.OUTGOING -> getOutgoingFriendRequestsUseCase()
                }
                emit(result to tab)
            }
            .onEach { (result, tab) ->
                when (result) {
                    is ResultWrapper.Success -> {
                        val baseContent = (viewState as? FriendsViewState.Content) ?: FriendsViewState.Content(currentTab = tab)
                        viewState = when (tab) {
                            RequestTab.FRIENDS -> baseContent.copy(friendsList = result.data, currentTab = tab, isTabLoading = false)
                            RequestTab.INCOMING -> baseContent.copy(incomingRequests = result.data, currentTab = tab, isTabLoading = false)
                            RequestTab.OUTGOING -> baseContent.copy(outgoingRequests = result.data, currentTab = tab, isTabLoading = false)
                        }
                    }
                    is ResultWrapper.Error -> {
                        if (viewState !is FriendsViewState.Content) {
                            viewState = FriendsViewState.Error(getErrorMessage(mapApiErrorToUserFriendly(result.error)))
                        } else {
                            updateContent { it.copy(isTabLoading = false) }
                            viewAction = FriendsAction.ShowError(getErrorMessage(mapApiErrorToUserFriendly(result.error)))
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun performAction(action: suspend () -> ResultWrapper<Unit>) {
        viewModelScope.launch {
            updateContent { it.copy(isActionPending = true) }
            when (val result = action()) {
                is ResultWrapper.Success -> {
                    updateContent { it.copy(isActionPending = false) }
                    loadTriggerFlow.emit(false)
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
