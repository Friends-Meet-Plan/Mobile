package friends.mobile.feature.wishplaces

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import friends.mobile.R
import friends.mobile.designsystem.components.ButtonFactory
import friends.mobile.designsystem.components.FormTextField
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val SWIPE_DELETE_THRESHOLD = 0.4f

@Composable
fun WishPlaceItem(
    place: WishPlace,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enableDelete: Boolean = true,
) {
    if (!enableDelete) {
        WishPlaceCard(place = place, onClick = onClick, modifier = modifier)
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val collapseProgress = remember { Animatable(1f) }
    var itemWidth by remember { mutableIntStateOf(1) }

    val heightFraction = collapseProgress.value
    val swipeProgress = (-offsetX.value / itemWidth).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val collapsedHeight = (placeable.height * heightFraction).roundToInt().coerceAtLeast(0)
                layout(placeable.width, collapsedHeight) {
                    placeable.placeRelative(0, 0)
                }
            }
            .clipToBounds()
            .onSizeChanged { size ->
                if (size.width > 0) itemWidth = size.width
            }
    ) {
        // Red delete background — always rendered, icon fades in with drag progress
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White.copy(alpha = (swipeProgress * 3f).coerceAtMost(1f)),
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(22.dp),
            )
        }

        // Card — draggable on top of the background
        WishPlaceCard(
            place = place,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(place.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val progress = -offsetX.value / itemWidth
                            if (progress >= SWIPE_DELETE_THRESHOLD) {
                                coroutineScope.launch {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = -itemWidth.toFloat(),
                                            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                                        )
                                    }
                                    delay(80)
                                    collapseProgress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                    )
                                    onDelete()
                                }
                            } else {
                                coroutineScope.launch {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium,
                                        ),
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(targetValue = 0f, animationSpec = spring())
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                offsetX.snapTo((offsetX.value + dragAmount).coerceAtMost(0f))
                            }
                        },
                    )
                }
        )
    }
}

@Composable
private fun WishPlaceCard(
    place: WishPlace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTheme.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
        ) {
            Text(
                text = place.title,
                style = DesignTheme.Typography.captionSemibold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            place.location?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = it,
                        style = DesignTheme.Typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule),
                )
                .padding(horizontal = DesignTheme.Spacing.sm, vertical = DesignTheme.Spacing.xs),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWishPlaceBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (String, String?, String?, String?) -> Unit,
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
        scrimColor = Color.Black.copy(alpha = 0.3f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTheme.Spacing.lg)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        ) {
            Text(stringResource(R.string.wish_places_add), style = DesignTheme.Typography.heading)

            FormTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = stringResource(R.string.label_title),
                modifier = Modifier.fillMaxWidth(),
            )
            FormTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = stringResource(R.string.label_location),
                modifier = Modifier.fillMaxWidth(),
            )
            FormTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = stringResource(R.string.label_description),
                modifier = Modifier.fillMaxWidth(),
            )
            FormTextField(
                value = link,
                onValueChange = { link = it },
                placeholder = stringResource(R.string.label_link),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(DesignTheme.Spacing.md))

            ButtonFactory.Primary(
                text = stringResource(R.string.create),
                onClick = {
                    onCreate(
                        title,
                        description.ifBlank { null },
                        location.ifBlank { null },
                        link.ifBlank { null },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = title.isNotBlank(),
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
            topEnd = DesignTheme.CornerRadius.medium,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTheme.Spacing.lg)
                .height(175.dp),
            verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = place.title,
                    style = DesignTheme.Typography.heading,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            place.location?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = it,
                        style = DesignTheme.Typography.body,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                    )
                }
            }

            if (!place.description.isNullOrEmpty()) {
                Text(
                    text = place.description!!,
                    style = DesignTheme.Typography.body,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadgeCompact(status = place.status.name)

                if (!place.link.isNullOrEmpty()) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        contentPadding = PaddingValues(
                            horizontal = DesignTheme.Spacing.md,
                            vertical = DesignTheme.Spacing.xs,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(DesignTheme.Spacing.xs))
                        Text(
                            stringResource(R.string.label_link),
                            style = DesignTheme.Typography.bodySmall,
                            color = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = stringResource(R.string.wish_places_added_date, place.createdAt.take(10)),
                style = DesignTheme.Typography.bodySmallest,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                shape = RoundedCornerShape(DesignTheme.CornerRadius.capsule),
            )
            .padding(
                horizontal = DesignTheme.Spacing.md,
                vertical = DesignTheme.Spacing.xs,
            ),
    )
}
