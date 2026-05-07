package friends.mobile.feature.friends.presentation

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.friends.domain.usecase.GetFriendsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * ViewModel for the friends screen.
 *
 * Follows the MVI (Model-View-Intent) pattern:
 *   - State: FriendsViewState (Loading, Error, Content with friends list)
 *   - Actions: FriendsAction (empty—no navigation)
 *   - Events: FriendsEvent (OnScreenOpen, OnLoadMore)
 *
 * Lifecycle:
 *   1. UI collects viewStates (StateFlow<FriendsViewState>)
 *   2. UI sends events via obtainEvent(event)
 *   3. ViewModel updates state based on events
 */
class FriendsViewModel :
    BaseViewModel<FriendsViewState, FriendsAction, FriendsEvent>(
        initState = FriendsViewState.Content(),
    ),
    KoinComponent {

    private val getFriendsUseCase: GetFriendsUseCase by inject()

    override fun obtainEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.OnScreenOpen -> onScreenOpen()
            is FriendsEvent.OnLoadMore -> onLoadMore(event.nextPage)
        }
    }

    private fun onScreenOpen() {
        viewModelScope.launch {
            viewState = FriendsViewState.Loading

            try {
                val friends = getFriendsUseCase(page = 1)
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

    private fun onLoadMore(nextPage: Int) {
        val currentState = viewState
        if (currentState !is FriendsViewState.Content) return

        viewModelScope.launch {
            val updatedState = currentState.copy(isLoadingMore = true)
            viewState = updatedState

            try {
                val newFriends = getFriendsUseCase(page = nextPage)
                viewState = FriendsViewState.Content(
                    friends = currentState.friends + newFriends,
                    isLoadingMore = false
                )
            } catch (_: NetworkException) {
                viewState = updatedState.copy(isLoadingMore = false)
            }
        }
    }
}
