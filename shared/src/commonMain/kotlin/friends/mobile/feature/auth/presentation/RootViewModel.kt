package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.core.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootViewModel : BaseViewModel<friends.mobile.feature.auth.presentation.RootViewState, Nothing, friends.mobile.feature.auth.presentation.RootEvent>(
    initState = _root_ide_package_.friends.mobile.feature.auth.presentation.RootViewState(),
), KoinComponent {

    private val repository: friends.mobile.feature.auth.domain.repository.AuthRepository by inject()

    init {
        _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(session = repository.getStoredSession())
    }

    override fun obtainEvent(event: friends.mobile.feature.auth.presentation.RootEvent) {
        when (event) {
            is friends.mobile.feature.auth.presentation.OnSessionStarted -> {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(session = event.session)
            }
            is friends.mobile.feature.auth.presentation.OnLogoutClick -> {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewModelScope.launch {
                    repository.logout()
                    _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(session = null)
                }
            }
        }
    }
}
