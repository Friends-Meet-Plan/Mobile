package friends.mobile.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.FormErrorMessage
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.events.presentation.CreateEventAction
import friends.mobile.feature.events.presentation.CreateEventEvent
import friends.mobile.feature.events.presentation.CreateEventViewModel
import friends.mobile.feature.events.presentation.CreateEventViewState
import friends.mobile.feature.friends.domain.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    LaunchedEffect(viewModel) {
        scope.launch {
            viewModel.viewActions.collect { action ->
                when (action) {
                    is CreateEventAction.NavigateToEventDetail -> onEventCreated(action.eventId)
                    is CreateEventAction.NavigateBack -> onBackClick()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is CreateEventViewState.Loading -> {
                LoadingView(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                    ButtonFactory.Primary(
                        text = "Back",
                        onClick = onBackClick,
                        modifier = Modifier.padding(top = DesignTheme.Spacing.lg),
                    )
                }
            }

            is CreateEventViewState.Content -> {
                val contentState = state as CreateEventViewState.Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = DesignTheme.Spacing.lg)
                        .padding(top = DesignTheme.Spacing.lg, bottom = DesignTheme.Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
                ) {
                    // Date header (outside scroll)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
                            )
                            .padding(vertical = DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                    ) {
                        Text(
                            text = "Event Date",
                            style = DesignTheme.Typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = contentState.selectedDate,
                            style = DesignTheme.Typography.heading,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (contentState.isLoadingFriends) {
                        // Loading friends fills remaining space
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "Loading available friends...",
                                    style = DesignTheme.Typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Scrollable form content
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = DesignTheme.Spacing.xl),
                            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
                        ) {
                            if (contentState.friendsError != null) {
                                item {
                                    FormErrorMessage(message = contentState.friendsError!!)
                                }
                            }

                            // Labeled form fields
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)) {
                                    LabeledFormField(
                                        label = "Title",
                                        icon = Icons.Default.Edit,
                                        value = contentState.title,
                                        onValueChange = {
                                            viewModel.obtainEvent(CreateEventEvent.OnTitleChanged(it))
                                        },
                                        placeholder = "Event title"
                                    )
                                    LabeledFormField(
                                        label = "Description",
                                        icon = Icons.Default.Description,
                                        value = contentState.description,
                                        onValueChange = {
                                            viewModel.obtainEvent(CreateEventEvent.OnDescriptionChanged(it))
                                        },
                                        placeholder = "Event description"
                                    )
                                    LabeledFormField(
                                        label = "Location",
                                        icon = Icons.Default.LocationOn,
                                        value = contentState.location,
                                        onValueChange = {
                                            viewModel.obtainEvent(CreateEventEvent.OnLocationChanged(it))
                                        },
                                        placeholder = "Event location"
                                    )
                                }
                            }

                            // Friends selection
                            item {
                                FriendsSelectionSection(
                                    selectedCount = contentState.selectedFriendIds.size,
                                    selectedFriends = contentState.availableFriends.filter {
                                        contentState.selectedFriendIds.contains(it.id)
                                    },
                                    onShowSheet = { showFriendsSheet = true },
                                    onRemoveFriend = { friendId ->
                                        viewModel.obtainEvent(CreateEventEvent.OnToggleFriend(friendId))
                                    }
                                )
                            }
                        }

                        // Create button fixed at bottom
                        ButtonFactory.Primary(
                            text = "Create Event",
                            onClick = { viewModel.obtainEvent(CreateEventEvent.OnCreateEvent) },
                            isLoading = contentState.isCreatingEvent,
                            isEnabled = contentState.isCreateButtonEnabled && !contentState.isCreatingEvent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showFriendsSheet && state is CreateEventViewState.Content) {
        val contentState = state as CreateEventViewState.Content
        ModalBottomSheet(
            onDismissRequest = { showFriendsSheet = false },
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
                onDone = { showFriendsSheet = false },
            )
        }
    }
}

@Composable
private fun LabeledFormField(
    label: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = DesignTheme.Typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FormTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FriendsSelectionSection(
    selectedCount: Int,
    selectedFriends: List<User>,
    onShowSheet: () -> Unit,
    onRemoveFriend: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onShowSheet() }
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (selectedCount > 0) {
                Text(
                    text = "$selectedCount selected",
                    style = DesignTheme.Typography.bodySmallest,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(DesignTheme.CornerRadius.small)
                        )
                        .padding(
                            horizontal = DesignTheme.Spacing.md,
                            vertical = DesignTheme.Spacing.xs
                        )
                )
            }
        }

        if (selectedFriends.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
                    )
                    .padding(DesignTheme.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                Text(
                    text = "Selected Friends",
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                ) {
                    selectedFriends.forEach { friend ->
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(DesignTheme.CornerRadius.small)
                                )
                                .padding(
                                    horizontal = DesignTheme.Spacing.md,
                                    vertical = DesignTheme.Spacing.xs
                                ),
                            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = friend.username,
                                style = DesignTheme.Typography.bodySmallest,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onRemoveFriend(friend.id) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Select Friends", style = DesignTheme.Typography.heading)
            ButtonFactory.Compact(text = "Done", onClick = onDone)
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = DesignTheme.Spacing.xxl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Loading friends...",
                            style = DesignTheme.Typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "No Friends Available",
                        style = DesignTheme.Typography.captionSemibold,
                    )
                    Text(
                        text = "No friends available on selected date",
                        style = DesignTheme.Typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            onToggle = { onToggleFriend(friend.id) },
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
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onToggle() }
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.username,
                style = DesignTheme.Typography.body,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!friend.bio.isNullOrEmpty()) {
                Text(
                    text = friend.bio!!,
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

