package friends.mobile.feature.auth.presentation.register

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel : BaseViewModel<RegisterViewState, RegisterAction, RegisterEvent>(
    initState = RegisterViewState.Content(),
), KoinComponent {

    private val registerUseCase: RegisterUseCase by inject()

    override fun obtainEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnRegisterClick -> onRegisterClick(
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
            viewState = RegisterViewState.Error("Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            viewState = RegisterViewState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            viewState = RegisterViewState.Loading

            try {
                registerUseCase(username, password)
                viewState = RegisterViewState.Content()
                viewAction = RegisterAction.RegisterSucceeded
            } catch (e: NetworkException.Conflict) {
                viewState = RegisterViewState.Error("Username is already taken")
            } catch (e: NetworkException.NetworkError) {
                viewState = RegisterViewState.Error("Network error, check your connection")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                viewState = RegisterViewState.Error("Something went wrong")
            }
        }
    }
}
