package friends.mobile.feature.events

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.FormErrorMessage
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.events.presentation.createevent.CreateEventAction
import friends.mobile.feature.events.presentation.createevent.CreateEventEvent
import friends.mobile.feature.events.presentation.createevent.CreateEventViewModel
import friends.mobile.feature.events.presentation.createevent.CreateEventViewState
import friends.mobile.feature.friends.domain.model.User
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEventView(
    selectedDate: String,
    onEventCreated: (eventId: String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: CreateEventViewModel = koinViewModel(parameters = { parametersOf(selectedDate) })
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
        topBar = { CreateEventTopBar(onBackClick = onBackClick) }
    ) { innerPadding ->
        when (state) {
            is CreateEventViewState.Loading -> {
                LoadingView(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    message = stringResource(R.string.event_loading_friends)
                )
            }
            is CreateEventViewState.Error -> {
                CreateEventErrorContent(
                    message = (state as CreateEventViewState.Error).message,
                    onBackClick = onBackClick,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                )
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
                    CreateEventDateHeader(date = contentState.selectedDate)
                    if (contentState.isLoadingFriends) {
                        FriendsLoadingIndicator(modifier = Modifier.weight(1f).fillMaxWidth())
                    } else {
                        CreateEventFormContent(
                            contentState = contentState,
                            onTitleChange = { viewModel.obtainEvent(CreateEventEvent.OnTitleChanged(it)) },
                            onDescriptionChange = { viewModel.obtainEvent(CreateEventEvent.OnDescriptionChanged(it)) },
                            onLocationChange = { viewModel.obtainEvent(CreateEventEvent.OnLocationChanged(it)) },
                            onToggleFriend = { viewModel.obtainEvent(CreateEventEvent.OnToggleFriend(it)) },
                            onShowFriendsSheet = { showFriendsSheet = true },
                            onCreateEvent = { viewModel.obtainEvent(CreateEventEvent.OnCreateEvent) },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
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
                onToggleFriend = { viewModel.obtainEvent(CreateEventEvent.OnToggleFriend(it)) },
                onDone = { showFriendsSheet = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEventTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.event_create_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun CreateEventErrorContent(
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ErrorBanner(message = message)
        ButtonFactory.Primary(
            text = stringResource(R.string.back),
            onClick = onBackClick,
            modifier = Modifier.padding(top = DesignTheme.Spacing.lg),
        )
    }
}

@Composable
private fun CreateEventDateHeader(date: String) {
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
            text = stringResource(R.string.event_section_date),
            style = DesignTheme.Typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = date,
            style = DesignTheme.Typography.heading,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FriendsLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = stringResource(R.string.event_loading_friends),
                style = DesignTheme.Typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateEventFormContent(
    contentState: CreateEventViewState.Content,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    onShowFriendsSheet: () -> Unit,
    onCreateEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)) {
                    LabeledFormField(
                        label = stringResource(R.string.label_title),
                        icon = Icons.Default.Edit,
                        value = contentState.title,
                        onValueChange = onTitleChange,
                        placeholder = stringResource(R.string.event_hint_title)
                    )
                    LabeledFormField(
                        label = stringResource(R.string.label_description),
                        icon = Icons.Default.Description,
                        value = contentState.description,
                        onValueChange = onDescriptionChange,
                        placeholder = stringResource(R.string.event_hint_description)
                    )
                    LabeledFormField(
                        label = stringResource(R.string.label_location),
                        icon = Icons.Default.LocationOn,
                        value = contentState.location,
                        onValueChange = onLocationChange,
                        placeholder = stringResource(R.string.event_hint_location)
                    )
                }
            }
            item {
                FriendsSelectionSection(
                    selectedCount = contentState.selectedFriendIds.size,
                    selectedFriends = contentState.availableFriends.filter {
                        contentState.selectedFriendIds.contains(it.id)
                    },
                    onShowSheet = onShowFriendsSheet,
                    onRemoveFriend = onToggleFriend,
                )
            }
        }

        ButtonFactory.Primary(
            text = stringResource(R.string.event_create_title),
            onClick = onCreateEvent,
            isLoading = contentState.isCreatingEvent,
            isEnabled = contentState.isCreateButtonEnabled && !contentState.isCreatingEvent,
            modifier = Modifier.fillMaxWidth()
        )
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
                text = stringResource(R.string.event_select_friends),
                style = DesignTheme.Typography.button,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (selectedCount > 0) {
                Text(
                    text = stringResource(R.string.event_selected_count, selectedCount),
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
                    text = stringResource(R.string.event_selected_friends),
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
                                contentDescription = stringResource(R.string.remove),
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

@OptIn(ExperimentalMaterial3Api::class)
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
            Text(text = stringResource(R.string.event_select_friends), style = DesignTheme.Typography.heading)
            ButtonFactory.Compact(text = stringResource(R.string.done), onClick = onDone)
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
                            text = stringResource(R.string.loading_friends),
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
                        text = stringResource(R.string.event_no_friends_title),
                        style = DesignTheme.Typography.captionSemibold,
                    )
                    Text(
                        text = stringResource(R.string.event_no_friends_subtitle),
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
