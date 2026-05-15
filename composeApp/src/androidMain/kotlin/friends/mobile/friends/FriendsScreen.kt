package friends.mobile.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.friends.FriendsAction
import friends.mobile.feature.friends.presentation.friends.FriendsEvent
import friends.mobile.feature.friends.presentation.friends.FriendsViewModel
import friends.mobile.feature.friends.presentation.friends.FriendsViewState
import friends.mobile.feature.friends.presentation.friends.RequestTab
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = koinViewModel()
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFriendId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is FriendsAction.ShowError -> {
                    snackbarHostState.showSnackbar(action.message)
                }
                is FriendsAction.NavigateToFriendProfile -> {
                    selectedFriendId = action.userId
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val currentState = state
            if (currentState is FriendsViewState.Content) {
                TabSelector(
                    selectedTab = currentState.currentTab,
                    onTabSelected = { viewModel.obtainEvent(FriendsEvent.OnTabSelected(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (val currentState = state) {
                is FriendsViewState.Loading -> {
                    LoadingSkeletons()
                }
                is FriendsViewState.Error -> {
                    ErrorBanner(
                        message = currentState.message,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is FriendsViewState.Content -> {
                    FriendsContent(
                        state = currentState,
                        onEvent = viewModel::obtainEvent
                    )
                }
            }
        }
    }

    FriendProfileBottomSheet(
        userId = selectedFriendId ?: "",
        isVisible = selectedFriendId != null,
        onDismiss = { selectedFriendId = null },
        onSheetDismissed = {
            viewModel.obtainEvent(FriendsEvent.ReloadCurrentTab)
        }
    )
}

@Composable
private fun FriendsContent(
    state: FriendsViewState.Content,
    onEvent: (FriendsEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            text = state.searchText,
            onTextChange = { onEvent(FriendsEvent.OnSearchUsers(it)) },
            onClear = { onEvent(FriendsEvent.OnSearchUsers("")) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val listToDisplay: List<User> = state.searchResults ?: when (state.currentTab) {
                RequestTab.FRIENDS -> state.friendsList
                RequestTab.INCOMING -> state.incomingRequests
                RequestTab.OUTGOING -> state.outgoingRequests
            }

            // Показываем список всегда, если он не пуст. 
            // Если пуст — показываем EmptyState только когда поиск НЕ активен.
            if (listToDisplay.isNotEmpty()) {
                UserListView(
                    users = listToDisplay,
                    onUserSelected = { user -> onEvent(FriendsEvent.OnUserClick(user.id)) }
                )
            } else if (!state.isSearching) {
                EmptyStateView(
                    currentTab = state.currentTab,
                    isSearchEmpty = state.searchResults != null,
                    searchText = state.searchText
                )
            }

            // Небольшой индикатор прогресса под поисковой строкой вместо мигающего экрана
            if (state.isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (state.isActionPending) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
