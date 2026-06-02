package friends.mobile.feature.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import friends.mobile.R
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.components.SearchBar
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
                is FriendsAction.ShowError -> snackbarHostState.showSnackbar(action.message)
                is FriendsAction.NavigateToFriendProfile -> selectedFriendId = action.userId
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
        ) {
            when (val currentState = state) {
                is FriendsViewState.Loading -> LoadingView(modifier = Modifier.fillMaxSize())
                is FriendsViewState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        ErrorBanner(
                            message = currentState.message,
                            modifier = Modifier.fillMaxWidth(),
                            onRetry = { viewModel.obtainEvent(FriendsEvent.ReloadCurrentTab) }
                        )
                    }
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
        onSheetDismissed = { viewModel.obtainEvent(FriendsEvent.ReloadCurrentTab) }
    )
}

@Composable
private fun FriendsContent(
    state: FriendsViewState.Content,
    onEvent: (FriendsEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            value = state.searchText,
            onValueChange = { onEvent(FriendsEvent.OnSearchUsers(it)) },
            onClear = { onEvent(FriendsEvent.OnSearchUsers("")) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTheme.Spacing.lg)
                .padding(top = DesignTheme.Spacing.lg, bottom = DesignTheme.Spacing.xs),
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val listToDisplay: List<User> = state.searchResults ?: when (state.currentTab) {
                RequestTab.FRIENDS -> state.friendsList
                RequestTab.INCOMING -> state.incomingRequests
                RequestTab.OUTGOING -> state.outgoingRequests
            }

            if (state.isSearching) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))
                    Text(
                        text = stringResource(R.string.loading_searching),
                        style = DesignTheme.Typography.body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else if (listToDisplay.isNotEmpty()) {
                UserListView(
                    users = listToDisplay,
                    currentTab = state.currentTab,
                    searchText = state.searchText,
                    onUserSelected = { user -> onEvent(FriendsEvent.OnUserClick(user.id)) }
                )
            } else {
                EmptyStateView(
                    currentTab = state.currentTab,
                    isSearchEmpty = state.searchResults != null,
                    searchText = state.searchText
                )
            }
        }

        TabSelector(
            selectedTab = state.currentTab,
            onTabSelected = { onEvent(FriendsEvent.OnTabSelected(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
