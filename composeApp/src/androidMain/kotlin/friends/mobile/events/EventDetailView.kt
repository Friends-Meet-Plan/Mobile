package friends.mobile.events

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.R
import friends.mobile.designkit.DesignTheme
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
                LoadingStateView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = DesignTheme.Colors.error
                    )
                    Text(
                        text = (state as EventDetailViewState.Error).message,
                        color = DesignTheme.Colors.error,
                        style = DesignTheme.Typography.body,
                        modifier = Modifier.padding(top = DesignTheme.Spacing.md)
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
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg),
    ) {
        // MARK: Event Image
        item {
            EventImageView()
        }

        // MARK: Title and Status
        item {
            Column(
                modifier = Modifier.padding(horizontal = DesignTheme.Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = DesignTheme.Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                    ) {
                        Text(
                            text = eventDetail.title,
                            style = DesignTheme.Typography.heading,
                            color = Color.Black,
                            maxLines = 3
                        )
                    }
                    StatusBadge(status = eventDetail.status)
                }
            }
        }

        // MARK: Details with Icons
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = DesignTheme.Spacing.lg)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                DetailRowWithIcon(
                    icon = Icons.Default.DateRange,
                    label = "Date",
                    value = eventDetail.date
                )

                eventDetail.time?.let { time ->
                    DetailRowWithIcon(
                        icon = Icons.Default.Schedule,
                        label = "Time",
                        value = time
                    )
                }

                eventDetail.location?.let { location ->
                    DetailRowWithIcon(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = location
                    )
                }
            }
        }

        // MARK: Description Section
        eventDetail.description?.let { description ->
            if (description.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = DesignTheme.Spacing.lg)
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF2F2F7),
                                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
                            )
                            .padding(DesignTheme.Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
                    ) {
                        Text(
                            text = "Description",
                            style = DesignTheme.Typography.button,
                            color = Color.Black
                        )
                        Text(
                            text = description,
                            style = DesignTheme.Typography.body,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // MARK: Participants Section
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = DesignTheme.Spacing.lg)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                Text(
                    text = "Participants",
                    style = DesignTheme.Typography.button,
                    color = Color.Black
                )

                if (eventDetail.participants.isEmpty()) {
                    Text(
                        text = "No participants yet",
                        style = DesignTheme.Typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(DesignTheme.Spacing.md)
                    )
                }
            }
        }

        items(
            eventDetail.participants,
            key = { it.userId }
        ) { participant ->
            ParticipantAvatarRow(
                participant = participant,
                colorIndex = eventDetail.participants.indexOf(participant),
                modifier = Modifier.padding(horizontal = DesignTheme.Spacing.lg)
            )
        }
    }
}

// MARK: - Status Badge

@Composable
private fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = status.replaceFirstChar { it.uppercase() },
        style = DesignTheme.Typography.bodySmall,
        color = Color.White,
        modifier = modifier
            .background(
                getResponseStatusColor(status),
                shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule)
            )
            .padding(
                horizontal = DesignTheme.Spacing.md,
                vertical = DesignTheme.Spacing.xs
            )
    )
}

// MARK: - Detail Row with Icon

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
                Color(0xFFF2F2F7),
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = DesignTheme.Colors.primary
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
        ) {
            Text(
                text = label,
                style = DesignTheme.Typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = DesignTheme.Typography.body,
                color = Color.Black
            )
        }
    }
}

// MARK: - Participant Avatar Row

@Composable
private fun ParticipantAvatarRow(
    participant: EventParticipant,
    colorIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF2F2F7),
                shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
            )
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with initials and status badge
        Box(
            modifier = Modifier.size(48.dp)
        ) {
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

            // Status badge icon
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(20.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                StatusBadgeIcon(participant.responseStatus)
            }
        }

        // User info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
        ) {
            Text(
                text = participant.username,
                style = DesignTheme.Typography.body,
                color = Color.Black
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant.role.replaceFirstChar { it.uppercase() },
                    style = DesignTheme.Typography.bodySmall,
                    color = Color.Gray
                )

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            getResponseStatusColor(participant.responseStatus),
                            shape = CircleShape
                        )
                )

                Text(
                    text = participant.responseStatus.replaceFirstChar { it.uppercase() },
                    style = DesignTheme.Typography.bodySmallest,
                    color = getResponseStatusColor(participant.responseStatus)
                )
            }
        }
    }
}

// MARK: - Status Badge Icon

@Composable
private fun StatusBadgeIcon(status: String) {
    when (status.lowercase()) {
        "accepted" -> {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF34BE48) // Green
            )
        }
        "declined" -> {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = DesignTheme.Colors.error
            )
        }
        "pending" -> {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFFFF9800) // Orange
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Gray
            )
        }
    }
}

// MARK: - Event Image View

@Composable
private fun EventImageView(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = DesignTheme.Spacing.lg),
        shape = RoundedCornerShape(DesignTheme.CornerRadius.medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F7)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pending_invitation_image),
                contentDescription = "Event image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// MARK: - Loading State View

@Composable
private fun LoadingStateView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.lg)
        ) {
            // Skeleton event image placeholder
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                // Skeleton title
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(DesignTheme.CornerRadius.small))
                )

                // Skeleton subtitle
                ShimmerBox(
                    modifier = Modifier
                        .width(200.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(DesignTheme.CornerRadius.small))
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
            ) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(DesignTheme.CornerRadius.small))
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
        ) {
            CircularProgressIndicator(
                color = DesignTheme.Colors.primary
            )
            Text(
                text = "Loading event details...",
                style = DesignTheme.Typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// MARK: - Shimmer Box

@Composable
private fun ShimmerBox(modifier: Modifier = Modifier) {
    val shimmerOffset = remember { mutableStateOf(-400f) }

    LaunchedEffect(Unit) {
        // Simple shimmer animation
        while (true) {
            // Animate shimmer offset
            shimmerOffset.value = -400f
            // In a real app, use animateFloatAsState or similar
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFFF2F2F7))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0f)
                    ),
                    start = Offset(shimmerOffset.value, 0f),
                    end = Offset(shimmerOffset.value + 400f, 0f)
                )
            )
    )
}

// MARK: - Avatar Color Palette

object AvatarColorPalette {
    private val colors = listOf(
        Color(red = 0.0f, green = 0.48f, blue = 1.0f),      // Blue
        Color(red = 0.34f, green = 0.78f, blue = 0.55f),    // Green
        Color(red = 1.0f, green = 0.58f, blue = 0.0f),      // Orange
        Color(red = 1.0f, green = 0.36f, blue = 0.48f),     // Pink
        Color(red = 0.67f, green = 0.43f, blue = 0.97f),    // Purple
        Color(red = 0.0f, green = 0.78f, blue = 0.73f),     // Teal
    )

    fun color(index: Int): Color {
        return colors[index % colors.size]
    }
}

// MARK: - Helper Functions

private fun getResponseStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "accepted" -> Color(0xFF34BE48)  // Green
        "declined" -> DesignTheme.Colors.error  // Red
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
