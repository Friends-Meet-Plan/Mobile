package friends.mobile.feature.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import friends.mobile.R
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.EventParticipant
import friends.mobile.feature.events.domain.model.ParticipationStatus

@Composable
fun PendingEventDetailContent(
    event: Event,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onAccept: (String) -> Unit = {},
    onDecline: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        when {
            isLoading -> {
                LoadingView(
                    modifier = Modifier.fillMaxWidth(),
                    message = stringResource(R.string.event_detail_loading)
                )
            }
            errorMessage != null -> {
                ErrorContent(message = errorMessage)
            }
            else -> {
                DetailContent(
                    event = event,
                    onAccept = onAccept,
                    onDecline = onDecline,
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DesignTheme.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = stringResource(R.string.pending_error),
            style = DesignTheme.Typography.heading,
        )
        Text(
            text = message,
            style = DesignTheme.Typography.body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DetailContent(
    event: Event,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xl),
    ) {
        item { HeaderSection(event = event) }
        item { EventDetailsSection(event = event) }
        item { ParticipantsSection(participants = event.participants) }
        item {
            ActionsSection(
                eventId = event.id,
                onAccept = onAccept,
                onDecline = onDecline,
            )
        }
    }
}

@Composable
private fun HeaderSection(event: Event) {
    val statusColor = statusColor(event.status)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
    ) {
        Text(
            text = event.title,
            style = DesignTheme.Typography.heading,
        )

        if (!event.description.isNullOrEmpty()) {
            Text(
                text = event.description!!,
                style = DesignTheme.Typography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier
                .background(
                    color = statusColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(DesignTheme.CornerRadius.medium),
                )
                .padding(
                    vertical = DesignTheme.Spacing.sm,
                    horizontal = DesignTheme.Spacing.md,
                ),
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = statusIcon(event.status),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = statusColor,
            )
            Text(
                text = event.status.replaceFirstChar { it.uppercase() },
                style = DesignTheme.Typography.captionSemibold,
                color = statusColor,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun EventDetailsSection(event: Event) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
    ) {
        Text(
            text = stringResource(R.string.pending_section_event_details),
            style = DesignTheme.Typography.bodySmallest,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        DetailRow(icon = Icons.Default.CalendarToday, label = stringResource(R.string.label_date), value = event.date)

        if (!event.time.isNullOrEmpty()) {
            DetailRow(icon = Icons.Default.Schedule, label = stringResource(R.string.label_time), value = event.time!!)
        }

        if (!event.location.isNullOrEmpty()) {
            DetailRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.label_location),
                value = event.location!!,
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)) {
            Text(
                text = label,
                style = DesignTheme.Typography.bodySmallest,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = DesignTheme.Typography.body,
            )
        }
    }
}

@Composable
private fun ParticipantsSection(participants: List<EventParticipant>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
    ) {
        Text(
            text = stringResource(R.string.pending_section_participants),
            style = DesignTheme.Typography.bodySmallest,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        participants.forEach { participant ->
            ParticipantCard(participant = participant)
        }
    }
}

@Composable
private fun ParticipantCard(participant: EventParticipant) {
    val statusColor = participantStatusColor(participant.status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium),
            )
            .padding(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = participant.username,
                style = DesignTheme.Typography.body,
            )
            Text(
                text = participant.role,
                style = DesignTheme.Typography.bodySmallest,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier
                .background(
                    color = statusColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(DesignTheme.CornerRadius.small),
                )
                .padding(
                    vertical = DesignTheme.Spacing.xs,
                    horizontal = DesignTheme.Spacing.sm,
                ),
            horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = participantStatusIcon(participant.status),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = statusColor,
            )
            Text(
                text = when (participant.status) {
                    ParticipationStatus.INVITED -> stringResource(R.string.pending_status)
                    else -> participant.status.name.lowercase().replaceFirstChar { it.uppercase() }
                },
                style = DesignTheme.Typography.bodySmallest,
                color = statusColor,
            )
        }
    }
}

@Composable
private fun ActionsSection(
    eventId: String,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignTheme.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
    ) {
        ButtonFactory.Primary(
            text = stringResource(R.string.pending_accept),
            onClick = { onAccept(eventId) },
        )
        ButtonFactory.Secondary(
            text = stringResource(R.string.pending_decline),
            onClick = { onDecline(eventId) },
        )
    }
}

@Composable
private fun statusColor(status: String): Color {
    return when (status.lowercase()) {
        "accepted" -> MaterialTheme.colorScheme.tertiary
        "declined" -> MaterialTheme.colorScheme.error
        "pending" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun statusIcon(status: String): ImageVector {
    return when (status.lowercase()) {
        "accepted" -> Icons.Default.CheckCircle
        "declined" -> Icons.Default.Cancel
        "pending" -> Icons.Default.AccessTime
        else -> Icons.AutoMirrored.Filled.Help
    }
}

@Composable
private fun participantStatusColor(status: ParticipationStatus): Color {
    return when (status) {
        ParticipationStatus.ACCEPTED -> MaterialTheme.colorScheme.tertiary
        ParticipationStatus.DECLINED -> MaterialTheme.colorScheme.error
        ParticipationStatus.INVITED -> Color(0xFFFF9800)
    }
}

private fun participantStatusIcon(status: ParticipationStatus): ImageVector {
    return when (status) {
        ParticipationStatus.ACCEPTED -> Icons.Default.CheckCircle
        ParticipationStatus.DECLINED -> Icons.Default.Cancel
        ParticipationStatus.INVITED -> Icons.Default.AccessTime
    }
}
