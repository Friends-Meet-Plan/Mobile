package friends.mobile.feature.auth.presentation

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : BaseViewModel<LoginViewState, LoginAction, LoginEvent>(
    initState = LoginViewState(),
), KoinComponent {

    private val repository: AuthRepository by inject()

    override fun obtainEvent(event: LoginEvent) {
        when (event) {
            is OnLoginClick -> onLoginClick(
                username = event.username,
                password = event.password,
            )
        }
    }

    private fun onLoginClick(username: String, password: String) {
        if (username.isBlank()) {
            viewState = viewState.copy(errorMessage = "Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            viewState = viewState.copy(errorMessage = "Password cannot be empty")
            return
        }

        viewModelScope.launch {
            viewState = viewState.copy(
                isLoading = true,
                errorMessage = null,
            )

            try {
                val session = repository.login(username, password)
                viewState = viewState.copy(isLoading = false)
                viewAction = LoginSucceeded(session)
            } catch (e: NetworkException.InvalidCredentials) {
                viewState = viewState.copy(
                    isLoading = false,
                    errorMessage = "Wrong username or password",
                )
            } catch (e: NetworkException.NetworkError) {
                viewState = viewState.copy(
                    isLoading = false,
                    errorMessage = "Network error, check your connection",
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                viewState = viewState.copy(
                    isLoading = false,
                    errorMessage = "Something went wrong",
                )
            }
        }
    }
}
