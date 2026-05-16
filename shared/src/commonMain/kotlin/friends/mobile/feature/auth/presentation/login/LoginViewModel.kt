package friends.mobile.feature.auth.presentation.login

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : BaseViewModel<LoginViewState, LoginAction, LoginEvent>(
    initState = LoginViewState.Content(),
), KoinComponent {

    private val loginUseCase: LoginUseCase by inject()

    override fun obtainEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChanged -> updateContent { it.copy(username = event.value) }
            is LoginEvent.OnPasswordChanged -> updateContent { it.copy(password = event.value) }
            is LoginEvent.OnLoginClick -> onLoginClick()
        }
    }

    private fun onLoginClick() {
        val currentState = viewState as? LoginViewState.Content ?: return
        
        if (currentState.username.isBlank()) {
            viewAction = LoginAction.ShowMessage("Username cannot be empty")
            return
        }
        if (currentState.password.isBlank()) {
            viewAction = LoginAction.ShowMessage("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            val username = currentState.username
            val password = currentState.password
            
            viewState = LoginViewState.Loading

            when (val result = loginUseCase(username, password)) {
                is ResultWrapper.Success -> {
                    viewState = LoginViewState.Content()
                    viewAction = LoginAction.NavigateToHome(result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    val message = getErrorMessage(userError)
                    
                    // Stay in Content state so the form remains visible
                    viewState = currentState.copy(isLoggingIn = false)
                    // Send error as a side effect (Action) for the Alert/Snackbar
                    viewAction = LoginAction.ShowMessage(message)
                }
            }
        }
    }

    private fun updateContent(transform: (LoginViewState.Content) -> LoginViewState.Content) {
        val current = (viewState as? LoginViewState.Content) ?: LoginViewState.Content()
        viewState = transform(current)
    }
}
