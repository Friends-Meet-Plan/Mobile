package friends.mobile.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.events.presentation.CreateEventAction
import friends.mobile.feature.events.presentation.CreateEventEvent
import friends.mobile.feature.events.presentation.CreateEventViewModel
import friends.mobile.feature.events.presentation.CreateEventViewState
import friends.mobile.feature.friends.domain.model.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventView(
    selectedDate: String,
    onEventCreated: (eventId: String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: CreateEventViewModel = viewModel(
        factory = CreateEventViewModelFactory(selectedDate)
    )
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    var showFriendsSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        logScreenOpen("launch_create_event")
    }

    LaunchedEffect(viewModel) {
        scope.launch {
            viewModel.viewActions.collect { action ->
                when (action) {
                    is CreateEventAction.NavigateToEventDetail -> {
                        onEventCreated(action.eventId)
                    }
                    is CreateEventAction.NavigateBack -> {
                        onBackClick()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is CreateEventViewState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is CreateEventViewState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = (state as CreateEventViewState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text("Back")
                    }
                }
            }

            is CreateEventViewState.Content -> {
                val contentState = state as CreateEventViewState.Content
                CreateEventContent(
                    state = contentState,
                    showFriendsSheet = showFriendsSheet,
                    onShowFriendsSheet = { showFriendsSheet = it },
                    onTitleChanged = { title ->
                        viewModel.obtainEvent(CreateEventEvent.OnTitleChanged(title))
                    },
                    onDescriptionChanged = { description ->
                        viewModel.obtainEvent(CreateEventEvent.OnDescriptionChanged(description))
                    },
                    onLocationChanged = { location ->
                        viewModel.obtainEvent(CreateEventEvent.OnLocationChanged(location))
                    },
                    onToggleFriend = { friendId ->
                        viewModel.obtainEvent(CreateEventEvent.OnToggleFriend(friendId))
                    },
                    onCreateClick = {
                        viewModel.obtainEvent(CreateEventEvent.OnCreateEvent)
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    if (showFriendsSheet && state is CreateEventViewState.Content) {
        val contentState = state as CreateEventViewState.Content
        ModalBottomSheet(
            onDismissRequest = {
                showFriendsSheet = false
            },
            sheetState = sheetState,
        ) {
            FriendsSelectionSheet(
                isLoading = contentState.isLoadingFriends,
                error = contentState.friendsError,
                friends = contentState.availableFriends,
                selectedFriendIds = contentState.selectedFriendIds,
                onToggleFriend = { friendId ->
                    viewModel.obtainEvent(CreateEventEvent.OnToggleFriend(friendId))
                },
                onDone = {
                    showFriendsSheet = false
                },
            )
        }
    }
}

@Composable
private fun CreateEventContent(
    state: CreateEventViewState.Content,
    showFriendsSheet: Boolean,
    onShowFriendsSheet: (Boolean) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Display selected date
            Text(
                text = "Event Date: ${state.selectedDate}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            // Show friends loading or error
            if (state.isLoadingFriends) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            if (state.friendsError != null) {
                Text(
                    text = state.friendsError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            // Title field
            TextField(
                value = state.title,
                onValueChange = onTitleChanged,
                label = { Text("Title *") },
                placeholder = { Text("Event title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Description field
            TextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Description") },
                placeholder = { Text("Event description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            // Location field
            TextField(
                value = state.location,
                onValueChange = onLocationChanged,
                label = { Text("Location") },
                placeholder = { Text("Event location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Select Friends button
            Button(
                onClick = {
                    onShowFriendsSheet(true)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Select Friends")
            }

            // Show selected friends
            if (state.selectedFriendIds.isNotEmpty()) {
                val selectedFriendsCount = state.selectedFriendIds.size
                val selectedFriendsList =
                    state.availableFriends.filter { state.selectedFriendIds.contains(it.id) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Selected Friends: $selectedFriendsCount",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    selectedFriendsList.forEach { friend ->
                        Text(
                            text = friend.username,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // Create button
            Button(
                onClick = onCreateClick,
                enabled = state.isCreateButtonEnabled && !state.isCreatingEvent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isCreatingEvent) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(if (state.isCreatingEvent) "Creating..." else "Create")
            }

            if (state.isCreatingEvent) {
                // Additional loading indicator for better UX
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun FriendsSelectionSheet(
    isLoading: Boolean,
    error: String?,
    friends: List<User>,
    selectedFriendIds: Set<String>,
    onToggleFriend: (String) -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Select Friends",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            friends.isEmpty() -> {
                Text(
                    text = "No friends available on selected date",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(friends, key = { it.id }) { friend ->
                        FriendSelectionItem(
                            friend = friend,
                            isSelected = selectedFriendIds.contains(friend.id),
                            onToggle = {
                                onToggleFriend(friend.id)
                            },
                        )
                    }
                }
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun FriendSelectionItem(
    friend: User,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = friend.username,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(12.dp),
        )
    }
}

private fun logScreenOpen(screenName: String) {
    // TODO: Wire Firebase Analytics here
    // FirebaseAnalytics.getInstance().logEvent(
    //     "screen_view",
    //     Bundle().apply {
    //         putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
    //     }
    // )
}
