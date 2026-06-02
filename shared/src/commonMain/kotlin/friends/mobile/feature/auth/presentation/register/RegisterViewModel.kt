package friends.mobile.feature.auth.presentation.register

import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class RegisterViewModel : BaseViewModel<RegisterViewState, RegisterAction, RegisterEvent>(
    initState = RegisterViewState.Content(),
    screenName = AnalyticsEvent.LAUNCH_REGISTER,
) {

    private val registerUseCase: RegisterUseCase by inject()

    override fun obtainEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnUsernameChanged -> updateContent { it.copy(username = event.value) }
            is RegisterEvent.OnPasswordChanged -> updateContent { it.copy(password = event.value) }
            is RegisterEvent.OnRegisterClick -> onRegisterClick()
            is RegisterEvent.OnRetryClick -> onRetryClick()
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
            val username = currentState.username
            val password = currentState.password

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

    private fun onRetryClick() {
        viewState = RegisterViewState.Content()
    }

    private fun updateContent(transform: (RegisterViewState.Content) -> RegisterViewState.Content) {
        val current = (viewState as? RegisterViewState.Content) ?: RegisterViewState.Content()
        viewState = transform(current)
    }
}
