package friends.mobile.friends

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.IndicatorFactory
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
            .background(DesignTheme.Colors.textField.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(DesignTheme.Colors.primary),
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
                color = Color.Black
            )
            user.bio?.let { bio ->
                if (bio.isNotEmpty()) {
                    Text(
                        text = bio,
                        style = DesignTheme.Typography.bodySmallest,
                        color = Color.Gray,
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
        RequestTab.FRIENDS -> IndicatorFactory.active()
        RequestTab.INCOMING -> IndicatorFactory.pending()
        RequestTab.OUTGOING -> IndicatorFactory.sent()
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
            tint = DesignTheme.Colors.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(DesignTheme.Spacing.lg))

        if (isSearchEmpty && searchText.isNotEmpty()) {
            Text(
                text = "No users found",
                style = DesignTheme.Typography.captionSemibold
            )
            Text(
                text = "Try searching with a different name",
                style = DesignTheme.Typography.bodySmall,
                color = Color.Gray
            )
        } else {
            Text(
                text = emptyStateTitle(currentTab),
                style = DesignTheme.Typography.captionSemibold
            )
            Text(
                text = emptyStateSubtitle(currentTab),
                style = DesignTheme.Typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private fun emptyStateTitle(tab: RequestTab): String = when (tab) {
    RequestTab.FRIENDS -> "No friends yet"
    RequestTab.INCOMING -> "No incoming requests"
    RequestTab.OUTGOING -> "No outgoing requests"
}

private fun emptyStateSubtitle(tab: RequestTab): String = when (tab) {
    RequestTab.FRIENDS -> "Search and send friend requests to get started"
    RequestTab.INCOMING -> "You will see incoming requests here"
    RequestTab.OUTGOING -> "Requests you have sent will appear here"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSelector(
    selectedTab: RequestTab,
    onTabSelected: (RequestTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTheme.Spacing.lg)
    ) {
        val tabs = RequestTab.entries
        tabs.forEachIndexed { index, tab ->
            SegmentedButton(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                label = {
                    Text(
                        text = when (tab) {
                            RequestTab.FRIENDS -> "Friends"
                            RequestTab.INCOMING -> "Incoming"
                            RequestTab.OUTGOING -> "Outgoing"
                        },
                        style = DesignTheme.Typography.bodySmall
                    )
                }
            )
        }
    }
}

@Composable
fun LoadingSkeletons() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTheme.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        repeat(8) { UserRowSkeleton() }
    }
}

@Composable
private fun UserRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(DesignTheme.Colors.textField)
            .padding(DesignTheme.Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(DesignTheme.Colors.textField.copy(alpha = 0.5f)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.2f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.12f)),
            )
        }
    }
}
