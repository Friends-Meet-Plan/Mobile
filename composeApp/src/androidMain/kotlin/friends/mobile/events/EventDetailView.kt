package friends.mobile.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.eventdetail.domain.model.EventDetail
import friends.mobile.feature.eventdetail.domain.model.EventParticipant
import friends.mobile.feature.eventdetail.presentation.EventDetailViewState
import friends.mobile.feature.eventdetail.presentation.EventDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailView(
    eventId: String,
    onBackClick: () -> Unit,
) {
    val viewModel: EventDetailViewModel = viewModel(
        factory = EventDetailViewModelFactory(eventId)
    )
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        logScreenOpen("launch_event_detail")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is EventDetailViewState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is EventDetailViewState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = (state as EventDetailViewState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            is EventDetailViewState.Content -> {
                val eventDetail = (state as EventDetailViewState.Content).eventDetail
                EventDetailContent(
                    eventDetail = eventDetail,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    eventDetail: EventDetail,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = eventDetail.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            Text(
                text = "Date: ${eventDetail.date}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (eventDetail.time != null) {
            item {
                Text(
                    text = "Time: ${eventDetail.time}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (eventDetail.location != null) {
            item {
                Text(
                    text = "Location: ${eventDetail.location}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item {
            Text(
                text = "Status: ${eventDetail.status}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (eventDetail.description != null) {
            item {
                Text(
                    text = "Description: ${eventDetail.description}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item {
            Text(
                text = "Participants",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        items(
            eventDetail.participants,
            key = { it.userId }
        ) { participant ->
            ParticipantRow(participant = participant)
        }
    }
}

@Composable
private fun ParticipantRow(participant: EventParticipant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = participant.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = participant.role,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }

        Text(
            text = participant.responseStatus,
            style = MaterialTheme.typography.labelMedium,
            color = getResponseStatusColor(participant.responseStatus),
        )
    }
}

private fun getResponseStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "accepted" -> Color(0xFF4CAF50)  // Green
        "declined" -> Color(0xFFF44336)  // Red
        "pending" -> Color(0xFFFF9800)   // Orange
        else -> Color.Gray
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
