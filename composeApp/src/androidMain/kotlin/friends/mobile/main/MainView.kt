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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.theme.DesignTheme
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0)
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val isLoading = state is MainViewState.Loading
    val errorMessage = (state as? MainViewState.Error)?.message
    val isRefreshing = (state as? MainViewState.Content)?.isRefreshing ?: false
    val activeEvents = (state as? MainViewState.Content)?.activeEvents ?: emptyList()
    val pendingEvents = (state as? MainViewState.Content)?.pendingEvents ?: emptyList()
    val hasNoEventsAtAll = activeEvents.isEmpty() && pendingEvents.isEmpty()

    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val pendingColor = Color(0xFFFF9800)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTheme.Spacing.lg)
                    .padding(top = DesignTheme.Spacing.xxl, bottom = DesignTheme.Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)) {
                        Text(
                            text = "My Events",
                            style = DesignTheme.Typography.heading,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manage active and upcoming plans",
                            style = DesignTheme.Typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onPendingEventsClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Pending Invitations",
                                tint = primaryColor
                            )
                        }
                    }
                }
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(DesignTheme.Spacing.lg))
                        Text(
                            text = errorMessage,
                            style = DesignTheme.Typography.body,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(DesignTheme.Spacing.lg))
                        ButtonFactory.Primary(
                            text = "Retry",
                            onClick = { viewModel.obtainEvent(MainEventAction.OnRefresh) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                hasNoEventsAtAll -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(DesignTheme.Spacing.lg))
                        Text(
                            text = "No events yet",
                            style = DesignTheme.Typography.captionSemibold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(DesignTheme.Spacing.sm))
                        Text(
                            text = "Tap + to create your first event",
                            style = DesignTheme.Typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = { viewModel.obtainEvent(MainEventAction.OnRefresh) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = DesignTheme.Spacing.lg,
                                end = DesignTheme.Spacing.lg,
                                top = DesignTheme.Spacing.md,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                        ) {
                            if (activeEvents.isNotEmpty()) {
                                item(key = "active_header") {
                                    EventSectionHeader(
                                        title = "Active",
                                        count = activeEvents.size,
                                        tint = tertiaryColor
                                    )
                                }
                                items(activeEvents, key = { "active_${it.id}" }) { event ->
                                    EventCard(
                                        event = event,
                                        isPending = false,
                                        onClick = { onEventDetailClick(event.id) }
                                    )
                                }
                            }

                            if (pendingEvents.isNotEmpty()) {
                                if (activeEvents.isNotEmpty()) {
                                    item(key = "section_gap") {
                                        Spacer(Modifier.height(DesignTheme.Spacing.lg))
                                    }
                                }
                                item(key = "pending_header") {
                                    EventSectionHeader(
                                        title = "Pending",
                                        count = pendingEvents.size,
                                        tint = pendingColor
                                    )
                                }
                                items(pendingEvents, key = { "pending_${it.id}" }) { event ->
                                    EventCard(
                                        event = event,
                                        isPending = true,
                                        onClick = { onEventDetailClick(event.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB bottom-trailing
        FloatingActionButton(
            onClick = { showDatePickerDialog = true },
            containerColor = primaryColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = DesignTheme.Spacing.lg, bottom = DesignTheme.Spacing.lg)
                .size(58.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Event"
            )
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
                                    false -> showBusyAlert = true
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
            title = { Text("You are busy on this day") },
            confirmButton = {
                Button(onClick = { showBusyAlert = false }) { Text("OK") }
            },
        )
    }
}

@Composable
private fun EventSectionHeader(title: String, count: Int, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignTheme.Spacing.sm, bottom = DesignTheme.Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = DesignTheme.Typography.captionSemibold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$count",
            style = DesignTheme.Typography.bodySmallest.copy(fontWeight = FontWeight.SemiBold),
            color = tint,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(tint.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit,
    isPending: Boolean = false,
) {
    val tintColor = if (isPending) Color(0xFFFF9800) else MaterialTheme.colorScheme.tertiary
    val badgeIcon = if (isPending) Icons.Default.Schedule else Icons.Default.CheckCircle
    val badgeText = if (isPending) "Pending" else "Active"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, tintColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.title,
                style = DesignTheme.Typography.captionSemibold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = DesignTheme.Spacing.sm)
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(tintColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = tintColor
                )
                Text(
                    text = badgeText,
                    style = DesignTheme.Typography.bodySmallest.copy(fontWeight = FontWeight.SemiBold),
                    color = tintColor
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
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
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = DesignTheme.Typography.bodySmallest,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                colors = androidx.compose.material3.TimePickerDefaults.colors(
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}
