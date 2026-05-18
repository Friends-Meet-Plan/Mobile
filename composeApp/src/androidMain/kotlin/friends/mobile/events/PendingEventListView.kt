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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.presentation.pendingevents.PendingAction
import friends.mobile.feature.events.presentation.pendingevents.PendingEvent
import friends.mobile.feature.events.presentation.pendingevents.PendingEventViewModel
import friends.mobile.feature.events.presentation.pendingevents.PendingViewState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingEventListView(
    onBackClick: () -> Unit,
) {
    val viewModel: PendingEventViewModel = koinViewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val actions by viewModel.viewActions.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        logScreenOpen("launch_pending_events")
    }

    LaunchedEffect(actions) {
        when (actions) {
            is PendingAction.ShowMessage -> {
                val message = (actions as PendingAction.ShowMessage).message
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            else -> {}
        }
    }

    val isLoading = state is PendingViewState.Loading
    val errorMessage = (state as? PendingViewState.Error)?.message
    val isRefreshing =
        (state as? PendingViewState.Content)?.isRefreshing ?: false
    val pendingEvents =
        (state as? PendingViewState.Content)?.events ?: emptyList()
    val selectedEventDetail =
        (state as? PendingViewState.Content)?.selectedEventDetail
    val isLoadingDetail =
        (state as? PendingViewState.Content)?.isLoadingDetail ?: false
    val detailError =
        (state as? PendingViewState.Content)?.detailError

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Invitations") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Button(
                        onClick = {
                            viewModel.obtainEvent(
                                friends.mobile.feature.events.presentation.pendingevents.PendingEvent.OnRefresh
                            )
                        },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text("Retry")
                    }
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
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No pending invitations",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                pendingEvents,
                                key = { it.id }
                            ) { event ->
                                PendingEventCard(
                                    event = event,
                                    onClick = {
                                        viewModel.obtainEvent(
                                            PendingEvent.OnEventClick(event.id)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    if (selectedEventDetail != null) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.closeEventDetail()
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            ),
        ) {
            PendingEventDetailContent(
                eventDetail = selectedEventDetail,
                isLoading = isLoadingDetail,
                errorMessage = detailError,
                onAccept = { eventId ->
                    viewModel.obtainEvent(
                        PendingEvent.OnAcceptEvent(eventId)
                    )
                },
                onDecline = { eventId ->
                    viewModel.obtainEvent(
                        PendingEvent.OnDeclineEvent(eventId)
                    )
                },
                modifier = Modifier.padding(bottom = 24.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Date: ${event.date}",
                style = MaterialTheme.typography.bodySmall,
            )
            if (!event.time.isNullOrEmpty()) {
                Text(
                    text = "Time: ${event.time}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!event.location.isNullOrEmpty()) {
                Text(
                    text = "Location: ${event.location}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!event.description.isNullOrEmpty()) {
                Text(
                    text = "Description: ${event.description}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = "Participants: ${event.participants.size}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
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
