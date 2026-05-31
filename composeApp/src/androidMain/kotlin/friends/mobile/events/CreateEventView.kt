package friends.mobile.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.ErrorBanner
import friends.mobile.designkit.components.FormErrorMessage
import friends.mobile.designkit.components.FormTextField
import friends.mobile.designkit.components.LoadingView
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
                LoadingView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    message = "Loading available friends..."
                )
            }

            is CreateEventViewState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(DesignTheme.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    ErrorBanner(message = (state as CreateEventViewState.Error).message)
                    ButtonFactory.primary(
                        text = "Back",
                        onClick = onBackClick,
                        modifier = Modifier.padding(top = DesignTheme.Spacing.lg),
                    )
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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        // Date header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = DesignTheme.Spacing.lg, horizontal = DesignTheme.Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Event Date",
                    style = DesignTheme.Typography.bodySmall,
                    color = Color.Gray,
                )
                Text(
                    text = state.selectedDate,
                    style = DesignTheme.Typography.heading,
                )
            }
        }

        // Friends loading indicator
        if (state.isLoadingFriends) {
            item {
                LoadingView(
                    modifier = Modifier.fillMaxWidth(),
                    message = "Loading available friends..."
                )
            }
        }

        // Friends error banner
        if (state.friendsError != null) {
            item {
                FormErrorMessage(message = state.friendsError!!)
            }
        }

        // Title field
        item {
            FormTextField(
                value = state.title,
                onValueChange = onTitleChanged,
                placeholder = "Event title",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Description field
        item {
            FormTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                placeholder = "Event description",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Location field
        item {
            FormTextField(
                value = state.location,
                onValueChange = onLocationChanged,
                placeholder = "Event location",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Select friends button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onShowFriendsSheet(true) }
                    .padding(DesignTheme.Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Select Friends",
                    style = DesignTheme.Typography.button,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (state.selectedFriendIds.isNotEmpty()) {
                    Text(
                        text = "${state.selectedFriendIds.size} selected",
                        style = DesignTheme.Typography.bodySmallest,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(DesignTheme.CornerRadius.small),
                            )
                            .padding(
                                horizontal = DesignTheme.Spacing.md,
                                vertical = DesignTheme.Spacing.xs,
                            ),
                    )
                }
            }
        }

        // Selected friends badges
        if (state.selectedFriendIds.isNotEmpty()) {
            val selectedFriendsList =
                state.availableFriends.filter { state.selectedFriendIds.contains(it.id) }
            items(selectedFriendsList, key = { "badge_${it.id}" }) { friend ->
                Row(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(DesignTheme.CornerRadius.small),
                        )
                        .padding(
                            horizontal = DesignTheme.Spacing.md,
                            vertical = DesignTheme.Spacing.xs,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = friend.username,
                        style = DesignTheme.Typography.bodySmallest,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Remove",
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onToggleFriend(friend.id) },
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Create button
        item {
            ButtonFactory.primary(
                text = "Create Event",
                onClick = onCreateClick,
                isLoading = state.isCreatingEvent,
                isEnabled = state.isCreateButtonEnabled && !state.isCreatingEvent,
            )
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
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select Friends",
                style = DesignTheme.Typography.heading,
            )
            ButtonFactory.compact(
                text = "Done",
                onClick = onDone,
            )
        }

        when {
            isLoading -> {
                LoadingView(
                    modifier = Modifier.fillMaxWidth(),
                    message = "Loading friends..."
                )
            }

            error != null -> {
                FormErrorMessage(message = error)
            }

            friends.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = DesignTheme.Spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray,
                    )
                    Text(
                        text = "No Friends Available",
                        style = DesignTheme.Typography.captionSemibold,
                    )
                    Text(
                        text = "No friends available on selected date",
                        style = DesignTheme.Typography.bodySmall,
                        color = Color.Gray,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = DesignTheme.Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
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
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onToggle() }
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.username,
                style = DesignTheme.Typography.body,
            )
            if (!friend.bio.isNullOrEmpty()) {
                Text(
                    text = friend.bio!!,
                    style = DesignTheme.Typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
        Spacer(modifier = Modifier.size(DesignTheme.Spacing.sm))
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
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
