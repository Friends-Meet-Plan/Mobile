package friends.mobile.calendar

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.ErrorBanner
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

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Activity", style = DesignTheme.Typography.heading)

        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))

        when (val current = state) {
            is BusyDaysViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DesignTheme.Colors.primary)
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
    Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)) {
        WeekdayLabels()
        BusyDaysGrid(busyDays = busyDays)
        Legend()
    }
}

@Composable
private fun WeekdayLabels() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
        weekdays.forEach { day ->
            Text(
                text = day,
                style = DesignTheme.Typography.bodySmallest,
                color = Color.Gray,
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
                color = if (isBusy) DesignTheme.Colors.secondaryAccent else DesignTheme.Colors.textField,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = DesignTheme.Typography.bodySmallest,
            color = if (isBusy) Color.White else Color.Gray
        )
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
    ) {
        LegendItem(color = DesignTheme.Colors.secondaryAccent, label = "Busy")
        LegendItem(color = DesignTheme.Colors.textField, label = "Not busy")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            style = DesignTheme.Typography.bodySmallest,
            color = Color.Gray
        )
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = DesignTheme.Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No busy days recorded",
            style = DesignTheme.Typography.body,
            color = Color.Gray
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
    ) {
        ErrorBanner(message = message, modifier = Modifier.fillMaxWidth())
        ButtonFactory.primary(text = "Retry", onClick = onRetry, modifier = Modifier.fillMaxWidth())
    }
}
