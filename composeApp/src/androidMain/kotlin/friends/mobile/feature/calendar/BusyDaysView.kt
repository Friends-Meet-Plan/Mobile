package friends.mobile.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designsystem.components.ErrorBanner
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.calendar.presentation.BusyDaysEvent
import friends.mobile.feature.calendar.presentation.BusyDaysViewModel
import friends.mobile.feature.calendar.presentation.BusyDaysViewState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BusyDaysView(
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: BusyDaysViewModel = koinViewModel(parameters = { parametersOf(userId) }),
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(BusyDaysEvent.OnRefresh)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        when (val current = state) {
            is BusyDaysViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is BusyDaysViewState.Error -> {
                ErrorContent(
                    message = current.message,
                    onRetry = { viewModel.obtainEvent(BusyDaysEvent.OnRetry) }
                )
            }
            is BusyDaysViewState.Content -> {
                if (current.calendarResponse.busyDays.isEmpty()) {
                    EmptyContent()
                } else {
                    ActivityGridView(busyDays = current.calendarResponse.busyDays)
                }
            }
        }
    }
}

@Composable
private fun ActivityGridView(busyDays: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
    ) {
        WeekdayLabels()
        BusyDaysGrid(busyDays = busyDays)
    }
}

@Composable
private fun WeekdayLabels() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val weekdays = listOf(
            stringResource(R.string.calendar_weekday_sun),
            stringResource(R.string.calendar_weekday_mon),
            stringResource(R.string.calendar_weekday_tue),
            stringResource(R.string.calendar_weekday_wed),
            stringResource(R.string.calendar_weekday_thu),
            stringResource(R.string.calendar_weekday_fri),
            stringResource(R.string.calendar_weekday_sat),
        )
        weekdays.forEach { day ->
            Text(
                text = day,
                style = DesignTheme.Typography.bodySmallest,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BusyDaysGrid(busyDays: List<String>) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val endDate = today.plus(27, DateTimeUnit.DAY)

    val dateRange = mutableListOf<LocalDate>()
    var currentDate = today
    while (currentDate <= endDate) {
        dateRange.add(currentDate)
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }

    val rows = dateRange.chunked(7)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rows.forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        DayCell(date = date, isBusy = busyDays.contains(date.toString()))
                    }
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, isBusy: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (isBusy) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = DesignTheme.Typography.bodySmallest,
            color = if (isBusy) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.calendar_no_busy_days),
            style = DesignTheme.Typography.body,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    ErrorBanner(
        message = message,
        modifier = Modifier.fillMaxWidth().padding(vertical = DesignTheme.Spacing.lg),
        onRetry = onRetry
    )
}
