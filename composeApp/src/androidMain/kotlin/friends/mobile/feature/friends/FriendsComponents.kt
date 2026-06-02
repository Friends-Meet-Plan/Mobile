package friends.mobile.feature.friends

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import friends.mobile.R
import friends.mobile.designsystem.components.IndicatorFactory
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.friends.domain.model.User
import friends.mobile.feature.friends.presentation.friends.RequestTab

@Composable
fun UserListView(
    users: List<User>,
    currentTab: RequestTab,
    searchText: String,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        items(users, key = { it.id }) { user ->
            UserRow(
                user = user,
                currentTab = currentTab,
                showStatusBadge = searchText.isEmpty(),
                onClick = { onUserSelected(user) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun UserRow(
    user: User,
    currentTab: RequestTab,
    showStatusBadge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.username.firstOrNull()?.uppercase() ?: "?",
                style = DesignTheme.Typography.captionSemibold.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
        ) {
            Text(
                text = user.username,
                style = DesignTheme.Typography.captionSemibold,
                color = MaterialTheme.colorScheme.onSurface
            )
            user.bio?.let { bio ->
                if (bio.isNotEmpty()) {
                    Text(
                        text = bio,
                        style = DesignTheme.Typography.bodySmallest,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        if (showStatusBadge) {
            StatusBadge(currentTab = currentTab)
        }
    }
}

@Composable
private fun StatusBadge(currentTab: RequestTab) {
    when (currentTab) {
        RequestTab.FRIENDS -> IndicatorFactory.Active()
        RequestTab.INCOMING -> IndicatorFactory.Pending()
        RequestTab.OUTGOING -> IndicatorFactory.Sent()
    }
}

@Composable
fun EmptyStateView(
    currentTab: RequestTab,
    isSearchEmpty: Boolean,
    searchText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
        ) {
            if (isSearchEmpty && searchText.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.friends_no_users_found),
                    style = DesignTheme.Typography.captionSemibold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.friends_try_different_name),
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = emptyStateTitle(currentTab),
                    style = DesignTheme.Typography.captionSemibold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = emptyStateSubtitle(currentTab),
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun emptyStateTitle(tab: RequestTab): String = when (tab) {
    RequestTab.FRIENDS -> stringResource(R.string.friends_no_friends)
    RequestTab.INCOMING -> stringResource(R.string.friends_no_incoming)
    RequestTab.OUTGOING -> stringResource(R.string.friends_no_outgoing)
}

@Composable
private fun emptyStateSubtitle(tab: RequestTab): String = when (tab) {
    RequestTab.FRIENDS -> stringResource(R.string.friends_hint_search_start)
    RequestTab.INCOMING -> stringResource(R.string.friends_hint_incoming)
    RequestTab.OUTGOING -> stringResource(R.string.friends_hint_outgoing)
}

@Composable
fun TabSelector(
    selectedTab: RequestTab,
    onTabSelected: (RequestTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = RequestTab.entries
    var localSelected by remember { mutableStateOf(selectedTab) }
    LaunchedEffect(selectedTab) { localSelected = selectedTab }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTheme.Spacing.lg)
                .padding(top = DesignTheme.Spacing.sm, bottom = DesignTheme.Spacing.lg)
                .clip(RoundedCornerShape(DesignTheme.CornerRadius.capsule))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(3.dp),
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == localSelected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(DesignTheme.CornerRadius.capsule))
                        .background(
                            if (isSelected) { MaterialTheme.colorScheme.surface } else { Color.Transparent }
                        )
                        .clickable {
                            localSelected = tab
                            onTabSelected(tab)
                        }
                        .padding(vertical = DesignTheme.Spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (tab) {
                            RequestTab.FRIENDS -> stringResource(R.string.friends_tab_friends)
                            RequestTab.INCOMING -> stringResource(R.string.friends_tab_incoming)
                            RequestTab.OUTGOING -> stringResource(R.string.friends_tab_outgoing)
                        },
                        style = if (isSelected) {
                            DesignTheme.Typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        } else {
                            DesignTheme.Typography.bodySmall
                        },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}
