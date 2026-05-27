package friends.mobile.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import friends.mobile.designkit.DesignTheme
import friends.mobile.designkit.FormErrorMessage
import friends.mobile.designkit.PrimaryButton
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.presentation.pendingevents.PendingAction
import friends.mobile.feature.events.presentation.pendingevents.PendingEvent
import friends.mobile.feature.events.presentation.pendingevents.PendingEventViewModel
import friends.mobile.feature.events.presentation.pendingevents.PendingViewState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
                is PendingAction.NavigateBack -> {
                    onBackClick()
                }
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
            TopAppBar(
                title = {
                    Text(
                        "Pending Invitations",
                        style = DesignTheme.Typography.heading
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.obtainEvent(PendingEvent.OnBackClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        when {
            isLoading && pendingEvents.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null && pendingEvents.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(DesignTheme.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    FormErrorMessage(message = errorMessage)
                    PrimaryButton(
                        text = "Retry",
                        onClick = {
                            viewModel.obtainEvent(PendingEvent.OnRefresh)
                        },
                        modifier = Modifier.padding(top = DesignTheme.Spacing.xl)
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    if (pendingEvents.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(DesignTheme.Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No pending invitations",
                                style = DesignTheme.Typography.body,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(DesignTheme.Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                        ) {
                            items(
                                pendingEvents,
                                key = { it.id }
                            ) { event ->
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignTheme.Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = DesignTheme.Colors.primary
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedEventDetail != null) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.obtainEvent(PendingEvent.OnDismissDetail)
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            ),
        ) {
            PendingEventDetailContent(
                event = selectedEventDetail,
                isLoading = isLoadingDetail,
                errorMessage = detailError,
                onAccept = { eventId ->
                    viewModel.obtainEvent(PendingEvent.OnAcceptEvent(eventId))
                },
                onDecline = { eventId ->
                    viewModel.obtainEvent(PendingEvent.OnDeclineEvent(eventId))
                },
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(DesignTheme.CornerRadius.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(DesignTheme.Spacing.lg)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
        ) {
            Text(
                text = event.title,
                style = DesignTheme.Typography.captionSemibold,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
            ) {
                Text(
                    text = "Date: ${event.date}",
                    style = DesignTheme.Typography.bodySmallest,
                    color = Color.Gray
                )

                if (!event.time.isNullOrEmpty()) {
                    Text(
                        text = "Time: ${event.time}",
                        style = DesignTheme.Typography.bodySmallest,
                        color = Color.Gray
                    )
                }

                if (!event.location.isNullOrEmpty()) {
                    Text(
                        text = "Location: ${event.location}",
                        style = DesignTheme.Typography.bodySmallest,
                        color = Color.Gray
                    )
                }

                if (!event.description.isNullOrEmpty()) {
                    Text(
                        text = "Description: ${event.description}",
                        style = DesignTheme.Typography.bodySmallest,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Participants: ${event.participants.size}",
                    style = DesignTheme.Typography.bodySmallest,
                    color = Color.Gray
                )
            }
        }
    }
}
