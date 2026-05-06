package friends.mobile.feature.auth.presentation

import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase
import friends.mobile.feature.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootViewModel :
    BaseViewModel<RootViewState, Nothing, RootEvent>(
        initState = RootViewState.Loading,
    ),
    KoinComponent {

    private val getStoredSessionUseCase: GetStoredSessionUseCase by inject()
    private val logoutUseCase: LogoutUseCase by inject()

    init {
        viewState = RootViewState.Content(session = getStoredSessionUseCase())
    }

    override fun obtainEvent(event: RootEvent) {
        when (event) {
            is RootEvent.OnSessionStarted -> {
                viewState = RootViewState.Content(session = event.session)
            }
            RootEvent.OnLogoutClick -> {
                viewModelScope.launch {
                    viewState = RootViewState.Loading
                    logoutUseCase()
                    viewState = RootViewState.Content(session = null)
                }
            }
        }
    }
}
