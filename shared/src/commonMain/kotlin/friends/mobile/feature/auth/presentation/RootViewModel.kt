package friends.mobile.feature.auth.presentation

import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase
import friends.mobile.feature.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch

class RootViewModel(
    private val getStoredSessionUseCase: GetStoredSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<RootViewState, Nothing, RootEvent>(
    initState = RootViewState.Loading,
) {

    init {
        loadSession()
    }

    override fun obtainEvent(event: RootEvent) {
        when (event) {
            is RootEvent.OnSessionStarted -> {
                viewState = RootViewState.Content(session = event.session)
            }
            is RootEvent.OnLogoutClick -> {
                onLogoutClick()
            }
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = getStoredSessionUseCase()
            viewState = RootViewState.Content(session = session)
        }
    }

    private fun onLogoutClick() {
        viewModelScope.launch {
            viewState = RootViewState.Loading
            logoutUseCase()
            viewState = RootViewState.Content(session = null)
        }
    }
}
