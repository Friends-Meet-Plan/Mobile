package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel : BaseViewModel<friends.mobile.feature.auth.presentation.RegisterViewState, friends.mobile.feature.auth.presentation.RegisterAction, friends.mobile.feature.auth.presentation.RegisterEvent>(
    initState = _root_ide_package_.friends.mobile.feature.auth.presentation.RegisterViewState(),
), KoinComponent {

    private val repository: friends.mobile.feature.auth.domain.repository.AuthRepository by inject()

    override fun obtainEvent(event: friends.mobile.feature.auth.presentation.RegisterEvent) {
        when (event) {
            is friends.mobile.feature.auth.presentation.OnRegisterClick -> onRegisterClick(
                username = event.username,
                password = event.password,
            )
        }
    }

    private fun onRegisterClick(
        username: String,
        password: String,
    ) {
        if (username.isBlank()) {
            _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(errorMessage = "Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(errorMessage = "Password cannot be empty")
            return
        }

        _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewModelScope.launch {
            _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(
                isLoading = true,
                errorMessage = null,
            )

            try {
                repository.register(username, password)
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(isLoading = false)
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewAction =
                    _root_ide_package_.friends.mobile.feature.auth.presentation.RegisterSucceeded()
            } catch (e: NetworkException.Conflict) {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(
                    isLoading = false,
                    errorMessage = "Username is already taken",
                )
            } catch (e: NetworkException.NetworkError) {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(
                    isLoading = false,
                    errorMessage = "Network error, check your connection",
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(
                    isLoading = false,
                    errorMessage = "Something went wrong",
                )
            }
        }
    }
}
