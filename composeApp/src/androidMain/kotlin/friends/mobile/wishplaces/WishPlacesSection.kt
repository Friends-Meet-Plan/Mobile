package friends.mobile.wishplaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    var showCreateSheet by remember {
        mutableStateOf(false)
    }

    var selectedPlace by remember {
        mutableStateOf<WishPlace?>(null)
    }

    LaunchedEffect(userId) {
        viewModel.obtainEvent(
            WishPlacesEvent.LoadPlaces(userId)
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.viewActions.collectLatest { action ->

            when (action) {

                is WishPlacesAction.PlaceCreated -> {
                    showCreateSheet = false
                }

                is WishPlacesAction.ShowError -> {
                    // snackbar if needed
                }
            }
        }
    }

    Column(modifier = modifier) {

        if (mode == WishPlacesMode.EDITABLE) {

            Button(
                onClick = {
                    showCreateSheet = true
                }
            ) {
                Text("Create Wish Place")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        when (val current = state) {

            is WishPlacesViewState.Loading -> {

                CircularProgressIndicator()
            }

            is WishPlacesViewState.Error -> {

                Text(
                    text = current.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WishPlacesViewState.Content -> {

                if (current.places.isEmpty()) {

                    Text("No wish places")
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    current.places.forEach { place ->

                        WishPlaceItem(
                            place = place,

                            onClick = {
                                selectedPlace = place
                            },

                            onDelete = {

                                if (mode == WishPlacesMode.EDITABLE) {

                                    viewModel.obtainEvent(
                                        WishPlacesEvent.ArchivePlace(
                                            userId = userId,
                                            id = place.id
                                        )
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

            onDismiss = {
                showCreateSheet = false
            },

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
            onDismiss = {
                selectedPlace = null
            }
        )
    }
}