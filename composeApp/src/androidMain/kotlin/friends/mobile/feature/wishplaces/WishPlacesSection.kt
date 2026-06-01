package friends.mobile.feature.wishplaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.R
import friends.mobile.designsystem.components.ErrorBanner
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
                is WishPlacesAction.ShowError -> {}
            }
        }
    }

    Column(modifier = modifier) {
        if (mode == WishPlacesMode.EDITABLE) {
            WishPlacesSectionHeader(onAddClick = { showCreateSheet = true })
        }

        when (val current = state) {
            is WishPlacesViewState.Loading -> WishPlacesLoadingContent()
            is WishPlacesViewState.Error -> {
                ErrorBanner(
                    message = current.message,
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { viewModel.obtainEvent(WishPlacesEvent.LoadPlaces(userId)) }
                )
            }
            is WishPlacesViewState.Content -> {
                WishPlacesContentBody(
                    places = current.places,
                    mode = mode,
                    userId = userId,
                    onPlaceClick = { selectedPlace = it },
                    onDeletePlace = { viewModel.obtainEvent(WishPlacesEvent.ArchivePlace(userId = userId, id = it)) },
                )
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

@Composable
private fun WishPlacesSectionHeader(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DesignTheme.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.wish_places_my),
            style = DesignTheme.Typography.heading,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onAddClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = stringResource(R.string.wish_places_add),
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WishPlacesLoadingContent() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun WishPlacesContentBody(
    places: List<WishPlace>,
    mode: WishPlacesMode,
    userId: String,
    onPlaceClick: (WishPlace) -> Unit,
    onDeletePlace: (String) -> Unit,
) {
    if (places.isEmpty()) {
        Text(
            text = stringResource(R.string.wish_places_empty),
            style = DesignTheme.Typography.body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(DesignTheme.Spacing.sm)) {
        places.forEach { place ->
            key(place.id) {
                WishPlaceItem(
                    place = place,
                    onClick = { onPlaceClick(place) },
                    onDelete = { if (mode == WishPlacesMode.EDITABLE) onDeletePlace(place.id) },
                    enableDelete = mode == WishPlacesMode.EDITABLE
                )
            }
        }
    }
}
