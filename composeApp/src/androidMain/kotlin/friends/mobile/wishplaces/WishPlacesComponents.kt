package friends.mobile.wishplaces

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import friends.mobile.designkit.theme.DesignTheme
import friends.mobile.designkit.components.ButtonFactory
import friends.mobile.designkit.components.FormTextField
import friends.mobile.feature.wishplaces.domain.model.WishPlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishPlaceItem(
    place: WishPlace,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enableDelete: Boolean = true
) {
    if (!enableDelete) {
        WishPlaceCard(place = place, onClick = onClick, modifier = modifier)
    } else {
        val dismissState = rememberSwipeToDismissBoxState(
            positionalThreshold = { totalDistance -> totalDistance * 0.4f },
            confirmValueChange = { value ->
                when (value) {
                    SwipeToDismissBoxValue.EndToStart -> {
                        onDelete()
                        true
                    }
                    else -> false
                }
            }
        )

        LaunchedEffect(place.id) {
            dismissState.reset()
        }

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val isDismissing = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
                val color by animateColorAsState(
                    targetValue = if (isDismissing) MaterialTheme.colorScheme.error else Color.Transparent,
                    label = "delete_color"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (isDismissing) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                }
            },
            modifier = modifier.padding(vertical = 4.dp)
        ) {
            WishPlaceCard(place = place, onClick = onClick)
        }
    }
}

@Composable
private fun WishPlaceCard(
    place: WishPlace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
        ) {
            Text(
                text = place.title,
                style = DesignTheme.Typography.captionSemibold,
                color = MaterialTheme.colorScheme.onSurface
            )
            place.location?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = it,
                        style = DesignTheme.Typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
        Text(
            text = place.status.name.lowercase().replaceFirstChar { it.uppercase() },
            style = DesignTheme.Typography.bodySmallest,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule)
                )
                .padding(horizontal = DesignTheme.Spacing.sm, vertical = DesignTheme.Spacing.xs)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWishPlaceBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (String, String?, String?, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTheme.Spacing.lg)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
        ) {
            Text("Add Wish Place", style = DesignTheme.Typography.heading)

            FormTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Title",
                modifier = Modifier.fillMaxWidth()
            )
            FormTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = "Location",
                modifier = Modifier.fillMaxWidth()
            )
            FormTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Description",
                modifier = Modifier.fillMaxWidth()
            )
            FormTextField(
                value = link,
                onValueChange = { link = it },
                placeholder = "Link",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(DesignTheme.Spacing.md))

            ButtonFactory.primary(
                text = "Create",
                onClick = {
                    onCreate(
                        title,
                        description.ifBlank { null },
                        location.ifBlank { null },
                        link.ifBlank { null }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = title.isNotBlank()
            )

            Spacer(modifier = Modifier.height(DesignTheme.Spacing.xxxl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishPlaceDetailBottomSheet(place: WishPlace, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Black.copy(alpha = 0.3f),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(
            topStart = DesignTheme.CornerRadius.medium,
            topEnd = DesignTheme.CornerRadius.medium
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTheme.Spacing.lg)
                .height(175.dp),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = place.title,
                    style = DesignTheme.Typography.heading,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            place.location?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = it,
                        style = DesignTheme.Typography.body,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            if (!place.description.isNullOrEmpty()) {
                Text(
                    text = place.description!!,
                    style = DesignTheme.Typography.body,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadgeCompact(status = place.status.name)

                if (!place.link.isNullOrEmpty()) {
                    androidx.compose.material3.TextButton(
                        onClick = { },
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(
                            horizontal = DesignTheme.Spacing.md,
                            vertical = DesignTheme.Spacing.xs
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(DesignTheme.Spacing.xs))
                        Text(
                            "Link",
                            style = DesignTheme.Typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = "Added ${place.createdAt.take(10)}",
                style = DesignTheme.Typography.bodySmallest,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun StatusBadgeCompact(status: String) {
    Text(
        text = status.lowercase().replaceFirstChar { it.uppercase() },
        style = DesignTheme.Typography.bodySmallest,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule)
            )
            .padding(
                horizontal = DesignTheme.Spacing.md,
                vertical = DesignTheme.Spacing.xs
            )
    )
}
