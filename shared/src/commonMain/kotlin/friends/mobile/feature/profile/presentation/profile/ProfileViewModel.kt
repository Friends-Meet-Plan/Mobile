package friends.mobile.feature.profile.presentation.profile

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileViewModel :
    BaseViewModel<ProfileViewState, ProfileAction, ProfileEvent>(
        initState = ProfileViewState.Loading,
    ),
    KoinComponent {

    private val getMeUseCase: GetMeUseCase by inject()

    override fun obtainEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLoadProfile -> onLoadProfile()
            is ProfileEvent.OnLogoutClick -> onLogoutClick()
        }
    }

    private fun onLoadProfile() {
        viewModelScope.launch {
            viewState = ProfileViewState.Loading
            when (val result = getMeUseCase()) {
                is ResultWrapper.Success -> {
                    viewState = ProfileViewState.Content(profile = result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = ProfileViewState.Error(message = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onLogoutClick() {
        val currentContent = viewState as? ProfileViewState.Content
        if (currentContent != null) {
            viewState = currentContent.copy(isLoggingOut = true)
        }
        viewAction = ProfileAction.LogoutRequested
    }
}