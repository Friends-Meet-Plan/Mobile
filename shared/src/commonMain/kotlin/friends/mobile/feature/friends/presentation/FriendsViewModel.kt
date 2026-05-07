package friends.mobile.feature.friends.presentation

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FriendsViewModel :
    BaseViewModel<FriendsViewState, FriendsAction, FriendsEvent>(
        initState = FriendsViewState.Content(),
    ),
    KoinComponent {

    private val getFriendsUseCase: GetFriendsUseCase by inject()

    override fun obtainEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.ScreenOpened -> onScreenOpen()
        }
    }

    private fun onScreenOpen() {
        viewModelScope.launch {
            viewState = FriendsViewState.Loading

            try {
                val friends = getFriendsUseCase()
                viewState = FriendsViewState.Content(friends = friends)
            } catch (_: NetworkException.NetworkError) {
                viewState = FriendsViewState.Error("Network error, check your connection")
            } catch (_: NetworkException.Unauthorized) {
                viewState = FriendsViewState.Error("Unauthorized. Please login again.")
            } catch (_: NetworkException.UnknownError) {
                viewState = FriendsViewState.Error("Something went wrong")
            } catch (_: NetworkException) {
                viewState = FriendsViewState.Error("Failed to load friends")
            }
        }
    }
}
