package friends.mobile.feature.auth.presentation.login

import friends.mobile.core.network.NetworkException
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : BaseViewModel<LoginViewState, LoginAction, LoginEvent>(
    initState = LoginViewState.Content(),
), KoinComponent {

    private val loginUseCase: LoginUseCase by inject()

    override fun obtainEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnLoginClick -> onLoginClick(
                username = event.username,
                password = event.password,
            )
        }
    }

    private fun onLoginClick(username: String, password: String) {
        if (username.isBlank()) {
            viewState = LoginViewState.Error("Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            viewState = LoginViewState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            viewState = LoginViewState.Loading

            try {
                val session = loginUseCase(username, password)
                viewState = LoginViewState.Content()
                viewAction = LoginAction.LoginSucceeded(session)
            } catch (e: NetworkException.InvalidCredentials) {
                viewState = LoginViewState.Error("Wrong username or password")
            } catch (e: NetworkException.NetworkError) {
                viewState = LoginViewState.Error("Network error, check your connection")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                viewState = LoginViewState.Error("Something went wrong")
            }
        }
    }
}
