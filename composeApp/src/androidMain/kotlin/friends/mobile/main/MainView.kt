package friends.mobile.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.ErrorBanner
import friends.mobile.designkit.components.LoadingView
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.main.presentation.MainViewAction as MainEventAction
import friends.mobile.feature.main.presentation.MainViewModel
import friends.mobile.feature.main.presentation.MainViewState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0)

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(Unit) {
        logScreenOpen("launch_home")
    }

    val isLoading = state is MainViewState.Loading
    val errorMessage = (state as? MainViewState.Error)?.message
    val isRefreshing = (state as? MainViewState.Content)?.isRefreshing ?: false
    val activeEvents = (state as? MainViewState.Content)?.activeEvents ?: emptyList()
    val pendingEvents = (state as? MainViewState.Content)?.pendingEvents ?: emptyList()

    val eventsToDisplay = when (filterMode) {
        FilterMode.ACTIVE -> activeEvents
        FilterMode.PENDING -> pendingEvents
    }

    val hasNoEventsAtAll = activeEvents.isEmpty() && pendingEvents.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Your Events", style = DesignTheme.Typography.captionSemibold)
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (pendingEvents.isNotEmpty()) {
                                Badge {
                                    Text(
                                        text = pendingEvents.size.toString(),
                                        style = DesignTheme.Typography.bodySmallest,
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onPendingEventsClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Pending Invitations",
                                tint = DesignTheme.Colors.primary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDatePickerDialog = true },
                containerColor = DesignTheme.Colors.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Event")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            when {
                isLoading && activeEvents.isEmpty() -> {
                    LoadingView(message = "Loading events...")
                }

                errorMessage != null && activeEvents.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        ErrorBanner(message = errorMessage, modifier = Modifier.fillMaxWidth())
                        ButtonFactory.primary(
                            text = "Retry",
                            onClick = { viewModel.obtainEvent(MainEventAction.OnRefresh) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DesignTheme.Spacing.lg)
                        )
                    }
                }

                hasNoEventsAtAll -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "You have no events yet",
                            style = DesignTheme.Typography.captionSemibold,
                        )
                        Spacer(modifier = Modifier.height(DesignTheme.Spacing.sm))
                        Text(
                            text = "Tap + to create your first event",
                            style = DesignTheme.Typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxl))
                        ButtonFactory.primary(
                            text = "Create Event",
                            onClick = { showDatePickerDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignTheme.Spacing.lg)
                        ) {
                            FilterMode.entries.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = filterMode == mode,
                                    onClick = { filterMode = mode },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = FilterMode.entries.size
                                    ),
                                ) {
                                    val label = when (mode) {
                                        FilterMode.ACTIVE -> "Active (${activeEvents.size})"
                                        FilterMode.PENDING -> "Pending (${pendingEvents.size})"
                                    }
                                    Text(label, style = DesignTheme.Typography.bodySmall)
                                }
                            }
                        }

                        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { viewModel.obtainEvent(MainEventAction.OnRefresh) },
                            modifier = Modifier.weight(1f),
                        ) {
                            if (eventsToDisplay.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(DesignTheme.Spacing.lg),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = when (filterMode) {
                                            FilterMode.ACTIVE -> "No active events yet"
                                            FilterMode.PENDING -> "No pending invitations"
                                        },
                                        style = DesignTheme.Typography.body,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(DesignTheme.Spacing.lg),
                                    verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                                ) {
                                    items(eventsToDisplay, key = { it.id }) { event ->
                                        EventCard(
                                            event = event,
                                            onClick = { onEventDetailClick(event.id) },
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

    if (showDatePickerDialog && !showTimePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDatePickerDialog = false
                    showTimePickerDialog = true
                }) { Text("Next") }
            },
            dismissButton = {
                Button(onClick = { showDatePickerDialog = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerDialog) {
        DateTimePickerDialog(
            onDismissRequest = { showTimePickerDialog = false },
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
                    onClick = { showTimePickerDialog = false },
                    enabled = !isCheckingAvailability,
                ) { Text("Cancel") }
            },
            timePickerState = timePickerState,
        )
    }

    if (showBusyAlert) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showBusyAlert = false },
            title = { Text("Not Available") },
            text = { Text("You are busy on this day. Please select another date.") },
            confirmButton = {
                Button(onClick = { showBusyAlert = false }) { Text("OK") }
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
    val tintColor = if (isPending) Color(0xFFFF9500) else DesignTheme.Colors.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, tintColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = event.title,
                style = DesignTheme.Typography.captionSemibold,
                modifier = Modifier.weight(1f),
            )
            if (isPending) {
                Text(
                    text = "Pending",
                    style = DesignTheme.Typography.bodySmallest,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            Color(0xFFFF9500),
                            shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule)
                        )
                        .padding(
                            horizontal = DesignTheme.Spacing.sm,
                            vertical = DesignTheme.Spacing.xs
                        )
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventInfoChip(icon = Icons.Default.CalendarToday, text = event.date)
            event.time?.let { EventInfoChip(icon = Icons.Default.Schedule, text = it) }
            EventInfoChip(icon = Icons.Default.Group, text = "${event.participants.size}")
        }
    }
}

@Composable
private fun EventInfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.Gray
        )
        Text(text = text, style = DesignTheme.Typography.bodySmallest, color = Color.Gray)
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
            TimePicker(state = timePickerState, layoutType = TimePickerLayoutType.Vertical)
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
}
