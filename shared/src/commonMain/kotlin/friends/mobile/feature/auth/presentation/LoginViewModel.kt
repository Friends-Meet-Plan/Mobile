package friends.mobile.feature.auth.presentation

import friends.mobile.feature.auth.domain.repository.AuthRepository
import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : BaseViewModel<friends.mobile.feature.auth.presentation.LoginViewState, friends.mobile.feature.auth.presentation.LoginAction, friends.mobile.feature.auth.presentation.LoginEvent>(
    initState = _root_ide_package_.friends.mobile.feature.auth.presentation.LoginViewState(),
), KoinComponent {

    private val repository: friends.mobile.feature.auth.domain.repository.AuthRepository by inject()

    override fun obtainEvent(event: friends.mobile.feature.auth.presentation.LoginEvent) {
        when (event) {
            is friends.mobile.feature.auth.presentation.OnLoginClick -> onLoginClick(
                username = event.username,
                password = event.password,
            )
        }
    }

    private fun onLoginClick(username: String, password: String) {
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
                val session = repository.login(username, password)
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(isLoading = false)
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewAction =
                    _root_ide_package_.friends.mobile.feature.auth.presentation.LoginSucceeded(
                        session
                    )
            } catch (e: NetworkException.InvalidCredentials) {
                _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState = _root_ide_package_.friends.mobile.core.viewmodel.BaseViewModel.viewState.copy(
                    isLoading = false,
                    errorMessage = "Wrong username or password",
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
