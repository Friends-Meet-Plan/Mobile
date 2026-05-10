package friends.mobile.feature.auth.presentation.login

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel :
    BaseViewModel<LoginViewState, LoginAction, LoginEvent>(
        initState = LoginViewState.Content(),
    ),
    KoinComponent {

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

            when (val result = loginUseCase(username, password)) {
                is ResultWrapper.Success -> {
                    viewState = LoginViewState.Content()
                    viewAction = LoginAction.LoginSucceeded(result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = LoginViewState.Error(getErrorMessage(userError))
                }
            }
        }
    }
}
