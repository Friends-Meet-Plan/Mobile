package friends.mobile.feature.auth.presentation.register

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel : BaseViewModel<RegisterViewState, RegisterAction, RegisterEvent>(
    initState = RegisterViewState.Content(),
), KoinComponent {

    private val registerUseCase: RegisterUseCase by inject()

    override fun obtainEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnUsernameChanged -> updateContent { it.copy(username = event.value) }
            is RegisterEvent.OnPasswordChanged -> updateContent { it.copy(password = event.value) }
            is RegisterEvent.OnRegisterClick -> onRegisterClick()
        }
    }

    private fun onRegisterClick() {
        val currentState = viewState as? RegisterViewState.Content ?: return
        
        if (currentState.username.isBlank()) {
            viewState = RegisterViewState.Error("Username cannot be empty")
            return
        }
        if (currentState.password.isBlank()) {
            viewState = RegisterViewState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            viewState = RegisterViewState.Loading

            when (val result = registerUseCase(currentState.username, currentState.password)) {
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

    private fun updateContent(transform: (RegisterViewState.Content) -> RegisterViewState.Content) {
        (viewState as? RegisterViewState.Content)?.let {
            viewState = transform(it)
        }
    }
}
