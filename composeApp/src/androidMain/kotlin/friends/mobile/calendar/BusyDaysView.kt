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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        // TODO: Wire Firebase Analytics here
        // FirebaseAnalytics.getInstance().logEvent(
        //     "screen_view",
        //     Bundle().apply {
        //         putString(FirebaseAnalytics.Param.SCREEN_NAME, "busy_days")
        //         putString(FirebaseAnalytics.Param.SCREEN_CLASS, "BusyDaysView")
        //     }
        // )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Activity",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val current = state) {

            is BusyDaysViewState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            is BusyDaysViewState.Error -> {

                ErrorContent(
                    message = current.message,
                    onRetry = {
                        viewModel.obtainEvent(BusyDaysEvent.OnRetry)
                    }
                )
            }

            is BusyDaysViewState.Content -> {

                if (current.calendarResponse.busyDays.isEmpty()) {

                    EmptyContent()

                } else {

                    ActivityGridView(
                        busyDays = current.calendarResponse.busyDays
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityGridView(
    busyDays: List<String>,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        WeekdayLabels()

        Spacer(modifier = Modifier.height(4.dp))

        BusyDaysGrid(busyDays = busyDays)

        Spacer(modifier = Modifier.height(12.dp))

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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BusyDaysGrid(
    busyDays: List<String>,
) {

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val endDate = today.plus(30, DateTimeUnit.DAY)

    val dateRange = mutableListOf<LocalDate>()
    var currentDate = today
    while (currentDate <= endDate) {
        dateRange.add(currentDate)
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        items(dateRange) { date ->

            DayCell(
                date = date,
                isBusy = busyDays.contains(date.toString())
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isBusy: Boolean,
) {

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (isBusy) {
                    Color(0xFF4CAF50)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isBusy) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun Legend() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        LegendItem(
            color = Color(0xFF4CAF50),
            label = "Busy"
        )

        LegendItem(
            color = MaterialTheme.colorScheme.surfaceVariant,
            label = "Not busy"
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyContent() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "No busy days recorded",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )

        Button(
            onClick = onRetry
        ) {

            Text("Retry")
        }
    }
}
