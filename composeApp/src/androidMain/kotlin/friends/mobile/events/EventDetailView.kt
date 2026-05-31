package friends.mobile.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.R
import androidx.compose.material3.MaterialTheme
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.designsystem.components.LoadingView
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.EventParticipant
import friends.mobile.feature.events.domain.model.ParticipationStatus
import friends.mobile.feature.events.presentation.eventdetail.EventDetailViewModel
import friends.mobile.feature.events.presentation.eventdetail.EventDetailViewState

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val currentState = state) {
            is EventDetailViewState.Loading -> {
                LoadingView(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    message = "Loading event details..."
                )
            }

            is EventDetailViewState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(DesignTheme.Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = DesignTheme.Typography.body,
                        modifier = Modifier.padding(top = DesignTheme.Spacing.md)
                    )
                }
            }

            is EventDetailViewState.Content -> {
                EventDetailContent(
                    event = currentState.event,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    event: Event,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = DesignTheme.Spacing.lg,
            end = DesignTheme.Spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        item {
            EventImageView()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = event.title,
                    style = DesignTheme.Typography.heading,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(end = DesignTheme.Spacing.md),
                    maxLines = 3
                )
                StatusBadge(status = event.status)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)) {
                DetailRowWithIcon(
                    icon = Icons.Default.CalendarToday,
                    label = "Date",
                    value = event.date
                )
                event.time?.let {
                    DetailRowWithIcon(icon = Icons.Default.Schedule, label = "Time", value = it)
                }
                event.location?.let {
                    if (it.isNotEmpty()) {
                        DetailRowWithIcon(icon = Icons.Default.LocationOn, label = "Location", value = it)
                    }
                }
            }
        }

        event.description?.let { description ->
            if (description.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
                            )
                            .padding(DesignTheme.Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                    ) {
                        Text(
                            text = "Description",
                            style = DesignTheme.Typography.button,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = description,
                            style = DesignTheme.Typography.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)) {
                Text(
                    text = "Participants",
                    style = DesignTheme.Typography.button,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (event.participants.isEmpty()) {
                    Text(
                        text = "No participants yet",
                        style = DesignTheme.Typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(DesignTheme.Spacing.md)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)) {
                        event.participants.forEachIndexed { index, participant ->
                            ParticipantAvatarRow(
                                participant = participant,
                                colorIndex = index,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Text(
        text = status.replaceFirstChar { it.uppercase() },
        style = DesignTheme.Typography.bodySmall,
        color = Color.White,
        modifier = modifier
            .background(color, shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule))
            .padding(horizontal = DesignTheme.Spacing.md, vertical = DesignTheme.Spacing.xs)
    )
}

@Composable
private fun statusColor(status: String): Color {
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error
    return when (status.lowercase()) {
        "accepted", "confirmed" -> tertiary
        "declined" -> error
        "pending" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
private fun DetailRowWithIcon(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
        ) {
            Text(
                text = label,
                style = DesignTheme.Typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = DesignTheme.Typography.body,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ParticipantAvatarRow(
    participant: EventParticipant,
    colorIndex: Int,
    modifier: Modifier = Modifier
) {
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AvatarColorPalette.color(colorIndex), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.username.take(1).uppercase(),
                    style = DesignTheme.Typography.button,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                StatusBadgeIcon(participant.status, tertiary, error)
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
        ) {
            Text(
                text = participant.username,
                style = DesignTheme.Typography.body,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant.role.replaceFirstChar { it.uppercase() },
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val statusColor = participationStatusColor(participant.status, tertiary, error)
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(statusColor, shape = CircleShape)
                )
                Text(
                    text = when (participant.status) {
                        ParticipationStatus.INVITED -> "Pending"
                        else -> participant.status.name.lowercase().replaceFirstChar { it.uppercase() }
                    },
                    style = DesignTheme.Typography.bodySmallest,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun StatusBadgeIcon(status: ParticipationStatus, tertiary: Color, error: Color) {
    when (status) {
        ParticipationStatus.ACCEPTED -> Icon(
            imageVector = Icons.Default.Done,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = tertiary
        )
        ParticipationStatus.DECLINED -> Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = error
        )
        ParticipationStatus.INVITED -> Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color(0xFFFF9800)
        )
    }
}

private fun participationStatusColor(status: ParticipationStatus, tertiary: Color, error: Color): Color =
    when (status) {
        ParticipationStatus.ACCEPTED -> tertiary
        ParticipationStatus.DECLINED -> error
        ParticipationStatus.INVITED -> Color(0xFFFF9800)
    }

@Composable
private fun EventImageView(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.pending_invitation_image),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
    )
}

object AvatarColorPalette {
    private val colors = listOf(
        Color(red = 0.0f, green = 0.48f, blue = 1.0f),
        Color(red = 0.34f, green = 0.78f, blue = 0.55f),
        Color(red = 1.0f, green = 0.58f, blue = 0.0f),
        Color(red = 1.0f, green = 0.36f, blue = 0.48f),
        Color(red = 0.67f, green = 0.43f, blue = 0.97f),
        Color(red = 0.0f, green = 0.78f, blue = 0.73f),
    )

    fun color(index: Int): Color = colors[index % colors.size]
}
