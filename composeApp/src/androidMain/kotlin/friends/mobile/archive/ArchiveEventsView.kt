package friends.mobile.archive

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import friends.mobile.feature.archive.presentation.ArchiveEventsViewModel
import friends.mobile.feature.archive.presentation.ArchiveViewAction
import friends.mobile.feature.archive.presentation.ArchiveViewState
import friends.mobile.feature.events.domain.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveEventsView(
    onEventDetailClick: (eventId: String) -> Unit = {},
) {
    val viewModel: ArchiveEventsViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        logScreenOpen("launch_archive")
    }

    val isLoading = state is ArchiveViewState.Loading
    val errorMessage = (state as? ArchiveViewState.Error)?.message
    val isRefreshing = (state as? ArchiveViewState.Content)?.isRefreshing ?: false
    val archivedEvents = (state as? ArchiveViewState.Content)?.archivedEvents ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Archived Events", style = DesignTheme.Typography.captionSemibold) },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            when {
                isLoading && archivedEvents.isEmpty() -> {
                    LoadingView(message = "Loading events...")
                }

                errorMessage != null && archivedEvents.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(DesignTheme.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        ErrorBanner(message = errorMessage, modifier = Modifier.fillMaxWidth())
                        ButtonFactory.primary(
                            text = "Retry",
                            onClick = { viewModel.obtainEvent(ArchiveViewAction.OnRefresh) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DesignTheme.Spacing.lg)
                        )
                    }
                }

                archivedEvents.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = DesignTheme.Colors.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))
                        Text(
                            text = "No archived events",
                            style = DesignTheme.Typography.captionSemibold
                        )
                        Text(
                            text = "Events you've attended will appear here",
                            style = DesignTheme.Typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { viewModel.obtainEvent(ArchiveViewAction.OnRefresh) },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(DesignTheme.Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                        ) {
                            items(archivedEvents, key = { it.id }) { event ->
                                ArchivedEventCard(
                                    event = event,
                                    onClick = { onEventDetailClick(event.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivedEventCard(
    event: Event,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, DesignTheme.Colors.primary.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Text(
            text = event.title,
            style = DesignTheme.Typography.captionSemibold,
            modifier = Modifier.fillMaxWidth(),
        )

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

private fun logScreenOpen(screenName: String) {
    // TODO: Wire Firebase Analytics here
}
