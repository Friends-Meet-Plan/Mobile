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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import friends.mobile.R
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.ErrorBanner
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

    val isLoading = state is ArchiveViewState.Loading
    val errorMessage = (state as? ArchiveViewState.Error)?.message
    val isRefreshing = (state as? ArchiveViewState.Content)?.isRefreshing ?: false
    val archivedEvents = (state as? ArchiveViewState.Content)?.archivedEvents ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.archive_title), style = DesignTheme.Typography.heading) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignTheme.Spacing.lg),
                        contentAlignment = Alignment.Center,
                    ) {
                        ErrorBanner(
                            message = errorMessage,
                            onRetry = { viewModel.obtainEvent(ArchiveViewAction.OnRefresh) }
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
                            imageVector = Icons.Default.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTheme.Spacing.sm))
                        Text(
                            text = stringResource(R.string.archive_empty),
                            style = DesignTheme.Typography.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = { viewModel.obtainEvent(ArchiveViewAction.OnRefresh) },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                horizontal = DesignTheme.Spacing.lg,
                                vertical = DesignTheme.Spacing.lg
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
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
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f).padding(end = DesignTheme.Spacing.sm)
            )
            CompletedBadge()
        }

        Row(horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)) {
            InfoChip(icon = Icons.Default.CalendarToday, text = event.date)
            event.time?.let { InfoChip(icon = Icons.Default.Schedule, text = it) }
            InfoChip(icon = Icons.Default.Group, text = "${event.participants.size}")
        }
    }
}

@Composable
private fun CompletedBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.archive_badge_completed),
            style = DesignTheme.Typography.bodySmallest.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            style = DesignTheme.Typography.bodySmallest,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
