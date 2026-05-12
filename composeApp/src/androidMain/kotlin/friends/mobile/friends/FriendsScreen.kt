package friends.mobile.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.FriendsEvent
import friends.mobile.feature.friends.presentation.FriendsViewModel
import friends.mobile.feature.friends.presentation.FriendsViewState
import friends.mobile.feature.friends.presentation.RequestTab
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FriendsScreen() {
    val viewModel: FriendsViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableStateOf(RequestTab.FRIENDS) }
    var selectedFriendId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            // Handle actions if needed
        }
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(FriendsEvent.ScreenOpened)
    }

    // Handle tab selection changes
    LaunchedEffect(selectedTab) {
        viewModel.obtainEvent(FriendsEvent.OnTabSelected(selectedTab))
    }

    // Handle search
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            viewModel.obtainEvent(FriendsEvent.OnSearchUsers(searchText))
        }
    }

    val isLoading = state is FriendsViewState.Loading
    val errorMessage = (state as? FriendsViewState.Error)?.message
    val contentState = state as? FriendsViewState.Content

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SearchBar(
            text = searchText,
            onTextChange = { searchText = it },
            onClear = { searchText = "" },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        )

        if (errorMessage != null) {
            ErrorBanner(
                message = errorMessage,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (isLoading) {
                LoadingSkeletons()
            } else {
                contentState?.let { content ->
                    ContentListView(
                        content = content,
                        searchText = searchText,
                        onUserSelected = { selectedFriendId = it.id },
                    )
                }
            }
        }

        TabSelector(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
                if (searchText.isNotEmpty()) {
                    searchText = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
        )
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
private fun ContentListView(
    content: FriendsViewState.Content,
    searchText: String,
    onUserSelected: (User) -> Unit,
) {
    val searchResults = content.searchResults

    val listToDisplay: List<User> = if (searchResults != null) {
        searchResults
    } else {
        when (content.currentTab) {
            RequestTab.FRIENDS -> content.friendsList
            RequestTab.INCOMING -> content.incomingRequests
            RequestTab.OUTGOING -> content.outgoingRequests
        }
    }

    val isEmpty = listToDisplay.isEmpty()

    if (content.isSearching && searchText.isNotEmpty()) {
        SearchingStateView()
    } else if (isEmpty) {
        EmptyStateView(
            currentTab = content.currentTab,
            isSearchEmpty = searchResults?.isEmpty() == true,
            searchText = searchText,
        )
    } else {
        UserListView(
            users = listToDisplay,
            onUserSelected = onUserSelected,
        )
    }
}

@Composable
private fun SearchingStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Text(
            text = "Searching...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun LoadingSkeletons() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        items(8) {
            UserRowSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun UserListView(
    users: List<User>,
    onUserSelected: (User) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        items(users) { user ->
            UserRow(
                user = user,
                onClick = { onUserSelected(user) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    currentTab: RequestTab,
    isSearchEmpty: Boolean,
    searchText: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        EmptyStateIcon(
            modifier = Modifier.padding(bottom = 24.dp),
        )

        if (isSearchEmpty && searchText.isNotEmpty()) {
            Text(
                text = "No users found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = "Try searching with a different name",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                text = emptyStateTitle(currentTab),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = emptyStateSubtitle(currentTab),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun emptyStateTitle(tab: RequestTab): String {
    return when (tab) {
        RequestTab.FRIENDS -> "No friends yet"
        RequestTab.INCOMING -> "No incoming requests"
        RequestTab.OUTGOING -> "No outgoing requests"
    }
}

private fun emptyStateSubtitle(tab: RequestTab): String {
    return when (tab) {
        RequestTab.FRIENDS -> "Search and send friend requests to get started"
        RequestTab.INCOMING -> "You will see incoming requests here"
        RequestTab.OUTGOING -> "Requests you have sent will appear here"
    }
}
