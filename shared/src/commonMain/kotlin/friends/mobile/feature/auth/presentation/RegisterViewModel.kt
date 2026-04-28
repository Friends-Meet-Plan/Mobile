package friends.mobile.feature.auth.presentation

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel : BaseViewModel<RegisterViewState, RegisterAction, RegisterEvent>(
    initState = RegisterViewState(),
), KoinComponent {

    private val repository: AuthRepository by inject()

    override fun obtainEvent(event: RegisterEvent) {
        when (event) {
            is OnRegisterClick -> onRegisterClick(
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
                repository.register(username, password)
                viewState = viewState.copy(isLoading = false)
                viewAction = RegisterSucceeded()
            } catch (e: NetworkException.Conflict) {
                viewState = viewState.copy(
                    isLoading = false,
                    errorMessage = "Username is already taken",
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
