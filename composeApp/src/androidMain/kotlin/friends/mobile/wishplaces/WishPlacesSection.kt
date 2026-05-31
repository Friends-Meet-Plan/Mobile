package friends.mobile.wishplaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.feature.wishplaces.presentation.WishPlacesAction
import friends.mobile.feature.wishplaces.presentation.WishPlacesEvent
import friends.mobile.feature.wishplaces.presentation.WishPlacesViewModel
import friends.mobile.feature.wishplaces.presentation.WishPlacesViewState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun WishPlacesSection(
    userId: String,
    mode: WishPlacesMode,
    modifier: Modifier = Modifier,
    viewModel: WishPlacesViewModel = koinViewModel(),
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    var showCreateSheet by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<WishPlace?>(null) }

    LaunchedEffect(userId) {
        viewModel.obtainEvent(WishPlacesEvent.LoadPlaces(userId))
    }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->
            when (action) {
                is WishPlacesAction.PlaceCreated -> showCreateSheet = false
                is WishPlacesAction.ShowError -> { }
            }
        }
    }

    Column(modifier = modifier) {
        if (mode == WishPlacesMode.EDITABLE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = DesignTheme.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Wish Places",
                    style = DesignTheme.Typography.heading,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showCreateSheet = true },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Wish Place",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        when (val current = state) {
            is WishPlacesViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is WishPlacesViewState.Error -> {
                Text(
                    text = current.message,
                    style = DesignTheme.Typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WishPlacesViewState.Content -> {
                if (current.places.isEmpty()) {
                    Text(
                        text = "No wish places yet",
                        style = DesignTheme.Typography.body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)) {
                    current.places.forEach { place ->
                        WishPlaceItem(
                            place = place,
                            onClick = { selectedPlace = place },
                            onDelete = {
                                if (mode == WishPlacesMode.EDITABLE) {
                                    viewModel.obtainEvent(
                                        WishPlacesEvent.ArchivePlace(userId = userId, id = place.id)
                                    )
                                }
                            },
                            enableDelete = mode == WishPlacesMode.EDITABLE
                        )
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        CreateWishPlaceBottomSheet(
            onDismiss = { showCreateSheet = false },
            onCreate = { title, desc, loc, link ->
                viewModel.obtainEvent(
                    WishPlacesEvent.CreatePlace(
                        userId = userId,
                        title = title,
                        description = desc,
                        location = loc,
                        link = link
                    )
                )
            }
        )
    }

    selectedPlace?.let { place ->
        WishPlaceDetailBottomSheet(
            place = place,
            onDismiss = { selectedPlace = null }
        )
    }
}
