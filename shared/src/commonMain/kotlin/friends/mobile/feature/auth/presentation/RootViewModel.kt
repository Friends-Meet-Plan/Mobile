package friends.mobile.feature.auth.presentation

import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootViewModel : BaseViewModel<RootViewState, Nothing, RootEvent>(
    initState = RootViewState(),
), KoinComponent {

    private val repository: AuthRepository by inject()

    init {
        viewState = viewState.copy(session = repository.getStoredSession())
    }

    override fun obtainEvent(event: RootEvent) {
        when (event) {
            is OnSessionStarted -> {
                viewState = viewState.copy(session = event.session)
            }
            is OnLogoutClick -> {
                viewModelScope.launch {
                    repository.logout()
                    viewState = viewState.copy(session = null)
                }
            }
        }
    }
}
