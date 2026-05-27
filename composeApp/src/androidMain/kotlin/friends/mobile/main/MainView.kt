package friends.mobile.main

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.main.presentation.MainViewAction as MainEventAction
import friends.mobile.feature.main.presentation.MainViewModel
import friends.mobile.feature.main.presentation.MainViewState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val coroutineScope = rememberCoroutineScope()

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showBusyAlert by rememberSaveable { mutableStateOf(false) }
    var isCheckingAvailability by rememberSaveable { mutableStateOf(false) }
    var filterMode by rememberSaveable { mutableStateOf(FilterMode.ACTIVE) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0
    )

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

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

    val eventsToDisplay = when (filterMode) {
        FilterMode.ACTIVE -> activeEvents
        FilterMode.PENDING -> pendingEvents
    }

    // Detect unified empty state: both activeEvents AND pendingEvents are empty
    val hasNoEventsAtAll = activeEvents.isEmpty() && pendingEvents.isEmpty()

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDatePickerDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Event"
                )
            }
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

                hasNoEventsAtAll -> {
                    // Unified empty state: no events at all
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "You have no events, create it now!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showDatePickerDialog = true }) {
                            Text("Create first event")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                    ) {
                        // Filter Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                FilterMode.entries.forEachIndexed { index, mode ->
                                    SegmentedButton(
                                        selected = filterMode == mode,
                                        onClick = { filterMode = mode },
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        val label = when (mode) {
                                            FilterMode.ACTIVE -> "Active (${activeEvents.size})"
                                            FilterMode.PENDING -> "Pending (${pendingEvents.size})"
                                        }
                                        Text(label)
                                    }
                                }
                            }
                        }

                        // Events List with Pull-to-Refresh
                        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
                                viewModel.obtainEvent(MainEventAction.OnRefresh)
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                if (eventsToDisplay.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        val emptyMessage = when (filterMode) {
                                            FilterMode.ACTIVE -> "No active events yet"
                                            FilterMode.PENDING -> "No pending invitations"
                                        }
                                        Text(
                                            text = emptyMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        items(
                                            eventsToDisplay,
                                            key = { it.id }
                                        ) { event ->
                                            EventCard(
                                                event = event,
                                                onClick = {
                                                    onEventDetailClick(event.id)
                                                },
                                                isPending = filterMode == FilterMode.PENDING,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerDialog && !showTimePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePickerDialog = false
                        showTimePickerDialog = true
                    },
                ) {
                    Text("Next")
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

    if (showTimePickerDialog) {
        DateTimePickerDialog(
            onDismissRequest = {
                showTimePickerDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedDateMs = datePickerState.selectedDateMillis
                        if (selectedDateMs != null) {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = selectedDateMs
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }
                            val dateString = dateFormatter.format(calendar.time)
                            val dateTimeString = dateTimeFormatter.format(calendar.time)

                            isCheckingAvailability = true
                            coroutineScope.launch {
                                val availabilityResult = viewModel.checkAvailability(dateString)
                                isCheckingAvailability = false

                                when (availabilityResult) {

                                    true -> {
                                        onCreateEventClick(dateString)
                                        showTimePickerDialog = false
                                    }

                                    false -> {
                                        showBusyAlert = true
                                    }

                                    null -> {
                                        onCreateEventClick(dateString)
                                        showTimePickerDialog = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isCheckingAvailability,
                ) {
                    if (isCheckingAvailability) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Create")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showTimePickerDialog = false
                    },
                    enabled = !isCheckingAvailability,
                ) {
                    Text("Cancel")
                }
            },
            timePickerState = timePickerState,
        )
    }

    if (showBusyAlert) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showBusyAlert = false
            },
            title = {
                Text("Not Available")
            },
            text = {
                Text("You are busy on this day. Please select another date.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBusyAlert = false
                    },
                ) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit,
    isPending: Boolean = false,
) {
    // Light orange/peach background for pending events (similar to iOS)
    val backgroundColor = if (isPending) {
        Color(red = 1.0f, green = 0.97f, blue = 0.92f).copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
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
                                Color(red = 1.0f, green = 0.647f, blue = 0.0f), // Orange color
                                shape = MaterialTheme.shapes.small,
                            )
                            .clip(MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "Pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
            }

            // Date
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )

            // Time (if present)
            event.time?.let { time ->
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            // Participants with person icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray,
                )
                Text(
                    text = "${event.participants.size} participants",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    timePickerState: androidx.compose.material3.TimePickerState,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            TimePicker(
                state = timePickerState,
                layoutType = TimePickerLayoutType.Vertical,
            )
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}

enum class FilterMode {
    ACTIVE,
    PENDING,
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
