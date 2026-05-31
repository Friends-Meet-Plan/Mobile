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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.presentation.pendingevents.PendingAction
import friends.mobile.feature.events.presentation.pendingevents.PendingEvent
import friends.mobile.feature.events.presentation.pendingevents.PendingEventViewModel
import friends.mobile.feature.events.presentation.pendingevents.PendingViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingEventListView(
    onBackClick: () -> Unit,
) {
    val viewModel: PendingEventViewModel = koinViewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle(
        initialValue = PendingViewState.Loading
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is PendingAction.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = action.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is PendingAction.NavigateBack -> onBackClick()
            }
        }
    }

    val isLoading = state is PendingViewState.Loading
    val errorMessage = (state as? PendingViewState.Error)?.message
    val isRefreshing = (state as? PendingViewState.Content)?.isRefreshing ?: false
    val pendingEvents = (state as? PendingViewState.Content)?.events ?: emptyList()
    val selectedEventDetail = (state as? PendingViewState.Content)?.selectedEventDetail
    val isLoadingDetail = (state as? PendingViewState.Content)?.isLoadingDetail ?: false
    val detailError = (state as? PendingViewState.Content)?.detailError

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pending Invitations") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.obtainEvent(PendingEvent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when {
            isLoading && pendingEvents.isEmpty() -> {
                LoadingView(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    message = "Loading invitations..."
                )
            }

            errorMessage != null && pendingEvents.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(DesignTheme.Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
                ) {
                    ErrorBanner(message = errorMessage)
                    ButtonFactory.Primary(
                        text = "Retry",
                        onClick = { viewModel.obtainEvent(PendingEvent.OnRefresh) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    if (pendingEvents.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(DesignTheme.Spacing.xl),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
                                ) {
                                    Text(
                                        text = "No Pending Invitations",
                                        style = DesignTheme.Typography.captionSemibold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "You're all caught up!",
                                        style = DesignTheme.Typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(DesignTheme.Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
                        ) {
                            items(pendingEvents, key = { it.id }) { event ->
                                PendingEventCard(
                                    event = event,
                                    onClick = {
                                        viewModel.obtainEvent(PendingEvent.OnEventClick(event.id))
                                    }
                                )
                            }
                        }
                    }

                    if (isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(DesignTheme.Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            LoadingView(modifier = Modifier.fillMaxWidth(), message = "Refreshing...")
                        }
                    }
                }
            }
        }
    }

    if (selectedEventDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.obtainEvent(PendingEvent.OnDismissDetail) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            PendingEventDetailContent(
                event = selectedEventDetail,
                isLoading = isLoadingDetail,
                errorMessage = detailError,
                onAccept = { eventId -> viewModel.obtainEvent(PendingEvent.OnAcceptEvent(eventId)) },
                onDecline = { eventId -> viewModel.obtainEvent(PendingEvent.OnDeclineEvent(eventId)) },
                modifier = Modifier.padding(bottom = DesignTheme.Spacing.xxl)
            )
        }
    }
}

@Composable
private fun PendingEventCard(
    event: Event,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
            ) {
                Text(
                    text = event.title,
                    style = DesignTheme.Typography.captionSemibold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = event.date,
                    style = DesignTheme.Typography.bodySmallest,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "${event.participants.size}",
                        style = DesignTheme.Typography.bodySmallest,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!event.time.isNullOrEmpty()) {
                    Text(
                        text = event.time!!,
                        style = DesignTheme.Typography.bodySmallest,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = DesignTheme.Spacing.xs),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                text = "Awaiting your response",
                style = DesignTheme.Typography.bodySmallest,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
