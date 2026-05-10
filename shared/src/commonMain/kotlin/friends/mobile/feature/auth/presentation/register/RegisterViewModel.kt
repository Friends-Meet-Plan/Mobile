package friends.mobile.feature.auth.presentation.register

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel :
    BaseViewModel<RegisterViewState, RegisterAction, RegisterEvent>(
        initState = RegisterViewState.Content(),
    ),
    KoinComponent {

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

            when (val result = registerUseCase(username, password)) {
                is ResultWrapper.Success -> {
                    viewState = RegisterViewState.Content()
                    viewAction = RegisterAction.RegisterSucceeded
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = RegisterViewState.Error(getErrorMessage(userError))
                }
            }
        }
    }
}
