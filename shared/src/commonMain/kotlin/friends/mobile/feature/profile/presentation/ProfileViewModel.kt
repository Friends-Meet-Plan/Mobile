package friends.mobile.feature.profile.presentation

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
        initState = ProfileViewState(),
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
            viewState = viewState.copy(isLoading = true, error = null)
            when (val result = getMeUseCase()) {
                is ResultWrapper.Success -> {
                    viewState = viewState.copy(profile = result.data, isLoading = false)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = viewState.copy(isLoading = false, error = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onLogoutClick() {
        viewAction = ProfileAction.LogoutRequested
    }
}
