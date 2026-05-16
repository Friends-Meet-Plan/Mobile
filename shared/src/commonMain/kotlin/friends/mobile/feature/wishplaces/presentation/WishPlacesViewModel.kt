package friends.mobile.feature.wishplaces.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.wishplaces.domain.usecase.ArchiveWishPlaceUseCase
import friends.mobile.feature.wishplaces.domain.usecase.CreateWishPlaceUseCase
import friends.mobile.feature.wishplaces.domain.usecase.GetWishPlacesUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WishPlacesViewModel :
    BaseViewModel<
            WishPlacesViewState,
            WishPlacesAction,
            WishPlacesEvent
            >(
        initState = WishPlacesViewState.Loading,
    ),
    KoinComponent {

    private val getWishPlacesUseCase: GetWishPlacesUseCase by inject()
    private val createWishPlaceUseCase: CreateWishPlaceUseCase by inject()
    private val archiveWishPlaceUseCase: ArchiveWishPlaceUseCase by inject()

    override fun obtainEvent(event: WishPlacesEvent) {
        when (event) {
            is WishPlacesEvent.LoadPlaces -> {
                loadPlaces(event.userId)
            }

            is WishPlacesEvent.CreatePlace -> {
                createPlace(
                    userId = event.userId,
                    title = event.title,
                    description = event.description,
                    location = event.location,
                    link = event.link
                )
            }

            is WishPlacesEvent.ArchivePlace -> {
                archivePlace(
                    userId = event.userId,
                    id = event.id
                )
            }
        }
    }

    private fun loadPlaces(userId: String) {
        viewModelScope.launch {

            if (viewState !is WishPlacesViewState.Content) {
                viewState = WishPlacesViewState.Loading
            }

            when (val result = getWishPlacesUseCase(userId)) {

                is ResultWrapper.Success -> {
                    viewState = WishPlacesViewState.Content(
                        places = result.data
                    )
                }

                is ResultWrapper.Error -> {
                    val error = mapApiErrorToUserFriendly(result.error)

                    viewState = WishPlacesViewState.Error(
                        getErrorMessage(error)
                    )
                }
            }
        }
    }

    private fun createPlace(
        userId: String,
        title: String,
        description: String?,
        location: String?,
        link: String?,
    ) {
        viewModelScope.launch {

            updateContent {
                it.copy(isActionPending = true)
            }

            when (
                val result = createWishPlaceUseCase(
                    title,
                    description,
                    location,
                    link
                )
            ) {

                is ResultWrapper.Success -> {

                    loadPlaces(userId)

                    viewAction = WishPlacesAction.PlaceCreated
                }

                is ResultWrapper.Error -> {

                    val error = mapApiErrorToUserFriendly(result.error)

                    updateContent {
                        it.copy(isActionPending = false)
                    }

                    viewAction = WishPlacesAction.ShowError(
                        getErrorMessage(error)
                    )
                }
            }
        }
    }

    private fun archivePlace(
        userId: String,
        id: String,
    ) {

        val current =
            viewState as? WishPlacesViewState.Content
                ?: return

        val updated =
            current.places.filterNot { it.id == id }

        viewState = current.copy(
            places = updated,
            isActionPending = true
        )

        viewModelScope.launch {

            when (val result = archiveWishPlaceUseCase(id)) {

                is ResultWrapper.Success -> {

                    updateContent {
                        it.copy(isActionPending = false)
                    }
                }

                is ResultWrapper.Error -> {

                    loadPlaces(userId)

                    val error =
                        mapApiErrorToUserFriendly(result.error)

                    viewAction = WishPlacesAction.ShowError(
                        getErrorMessage(error)
                    )
                }
            }
        }
    }

    private fun updateContent(
        transform: (
            WishPlacesViewState.Content
        ) -> WishPlacesViewState.Content
    ) {
        (viewState as? WishPlacesViewState.Content)
            ?.let {
                viewState = transform(it)
            }
    }
}
//class WishPlacesViewModel :
//    BaseViewModel<WishPlacesViewState, WishPlacesAction, WishPlacesEvent>(
//        initState = WishPlacesViewState.Loading,
//    ),
//    KoinComponent {
//
//    private val getWishPlacesUseCase: GetWishPlacesUseCase by inject()
//    private val createWishPlaceUseCase: CreateWishPlaceUseCase by inject()
//    private val archiveWishPlaceUseCase: ArchiveWishPlaceUseCase by inject()
//
//    private var currentUserId: String? = null
//
//    override fun obtainEvent(event: WishPlacesEvent) {
//        when (event) {
//            is WishPlacesEvent.LoadPlaces -> loadPlaces(event.userId)
//            is WishPlacesEvent.CreatePlace -> createPlace(event.title, event.description, event.location, event.link)
//            is WishPlacesEvent.ArchivePlace -> archivePlace(event.id)
//        }
//    }
//
//    private fun loadPlaces(userId: String) {
//        currentUserId = userId
//        viewModelScope.launch {
//            if (viewState !is WishPlacesViewState.Content) {
//                viewState = WishPlacesViewState.Loading
//            } else {
//                updateContent { it.copy(isRefreshing = true) }
//            }
//
//            when (val result = getWishPlacesUseCase(userId)) {
//                is ResultWrapper.Success -> {
//                    viewState = WishPlacesViewState.Content(places = result.data)
//                }
//                is ResultWrapper.Error -> {
//                    val userError = mapApiErrorToUserFriendly(result.error)
//                    if (viewState !is WishPlacesViewState.Content) {
//                        viewState = WishPlacesViewState.Error(getErrorMessage(userError))
//                    } else {
//                        updateContent { it.copy(isRefreshing = false) }
//                        viewAction = WishPlacesAction.ShowError(getErrorMessage(userError))
//                    }
//                }
//            }
//        }
//    }
//
//    private fun createPlace(title: String, description: String?, location: String?, link: String?) {
//        val userId = currentUserId ?: return
//        viewModelScope.launch {
//            updateContent { it.copy(isActionPending = true) }
//            when (val result = createWishPlaceUseCase(title, description, location, link)) {
//                is ResultWrapper.Success -> {
//                    viewAction = WishPlacesAction.PlaceCreated
//                    loadPlaces(userId)
//                }
//                is ResultWrapper.Error -> {
//                    val userError = mapApiErrorToUserFriendly(result.error)
//                    updateContent { it.copy(isActionPending = false) }
//                    viewAction = WishPlacesAction.ShowError(getErrorMessage(userError))
//                }
//            }
//        }
//    }
//
//    private fun archivePlace(id: String) {
//        val userId = currentUserId ?: return
//        val currentState = viewState as? WishPlacesViewState.Content ?: return
//
//        // Оптимистичное удаление: убираем из списка сразу
//        val updatedList = currentState.places.filter { it.id != id }
//        viewState = currentState.copy(places = updatedList, isActionPending = true)
//
//        viewModelScope.launch {
//            when (val result = archiveWishPlaceUseCase(id)) {
//                is ResultWrapper.Success -> {
//                    // Просто выключаем лоадер, список уже обновлен
//                    updateContent { it.copy(isActionPending = false) }
//                }
//                is ResultWrapper.Error -> {
//                    // Если ошибка — возвращаем список назад (или перезагружаем)
//                    val userError = mapApiErrorToUserFriendly(result.error)
//                    viewAction = WishPlacesAction.ShowError(getErrorMessage(userError))
//                    loadPlaces(userId)
//                }
//            }
//        }
//    }
//
//    private fun updateContent(transform: (WishPlacesViewState.Content) -> WishPlacesViewState.Content) {
//        (viewState as? WishPlacesViewState.Content)?.let {
//            viewState = transform(it)
//        }
//    }
//}
