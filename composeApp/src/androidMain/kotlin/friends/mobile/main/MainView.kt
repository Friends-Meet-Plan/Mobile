package friends.mobile.main

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.main.domain.model.MainEvent
import friends.mobile.feature.main.presentation.MainViewAction as MainEventAction
import friends.mobile.feature.main.presentation.MainViewModel
import friends.mobile.feature.main.presentation.MainViewState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onEventDetailClick: (eventId: String) -> Unit,
    onCreateEventClick: (date: String) -> Unit,
    onPendingEventsClick: () -> Unit = {},
) {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(Unit) {
        // Log screen open event for analytics
        logScreenOpen("launch_home")
    }

    val isLoading = state is MainViewState.Loading
    val errorMessage = (state as? MainViewState.Error)?.message
    val isRefreshing =
        (state as? MainViewState.Content)?.isRefreshing ?: false
    val activeEvents =
        (state as? MainViewState.Content)?.activeEvents ?: emptyList()
    val pendingEvents =
        (state as? MainViewState.Content)?.pendingEvents ?: emptyList()

    val hasAnyEvents = activeEvents.isNotEmpty() || pendingEvents.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (pendingEvents.isNotEmpty()) {
                                Badge(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            shape = CircleShape
                                        )
                                        .size(20.dp),
                                ) {
                                    Text(
                                        text = pendingEvents.size.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError,
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onPendingEventsClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Pending Invitations"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                isLoading && activeEvents.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null && activeEvents.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                                viewModel.obtainEvent(MainEventAction.OnRefresh)
                            },
                            modifier = Modifier.padding(top = 16.dp),
                        ) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    if (!hasAnyEvents) {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "You have no events, create it now!",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Button(
                                    onClick = {
                                        showDatePickerDialog = true
                                    },
                                    modifier = Modifier.padding(top = 16.dp),
                                ) {
                                    Text("Create event")
                                }
                            }
                        }
                    } else {
                        // Events displayed in sections
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            // Active Events Section
                            if (activeEvents.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(
                                            text = "Active",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        activeEvents.forEach { event ->
                                            EventCard(
                                                event = event,
                                                onClick = {
                                                    onEventDetailClick(event.id)
                                                },
                                                isPending = false,
                                            )
                                        }
                                    }
                                }
                            }

                            // Pending Events Section
                            if (pendingEvents.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(
                                            text = "Pending",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        pendingEvents.forEach { event ->
                                            EventCard(
                                                event = event,
                                                onClick = {
                                                    onEventDetailClick(event.id)
                                                },
                                                isPending = true,
                                            )
                                        }
                                    }
                                }
                            }

                            // Loading indicator during refresh
                            if (isRefreshing) {
                                item {
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

                            // Create Event Button
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Button(
                                        onClick = {
                                            showDatePickerDialog = true
                                        },
                                    ) {
                                        Text("Create event")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedDateMs = datePickerState.selectedDateMillis
                        if (selectedDateMs != null) {
                            val dateString = dateFormatter.format(Date(selectedDateMs))
                            onCreateEventClick(dateString)
                            showDatePickerDialog = false
                        }
                    },
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDatePickerDialog = false
                    },
                ) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun EventCard(
    event: MainEvent,
    onClick: () -> Unit,
    isPending: Boolean = false,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Title with pending badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                if (isPending) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small,
                            )
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.small),
                    ) {
                        Text(
                            text = "Pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Date: ${event.date}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!event.time.isNullOrEmpty()) {
                Text(
                    text = "Time: ${event.time}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = "Participants: ${event.participantCount}",
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
