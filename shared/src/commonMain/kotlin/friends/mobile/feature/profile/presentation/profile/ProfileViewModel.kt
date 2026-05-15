package friends.mobile.feature.profile.presentation.profile

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getMeUseCase: GetMeUseCase,
) : BaseViewModel<ProfileViewState, ProfileAction, ProfileEvent>(
    initState = ProfileViewState.Loading,
) {

    init {
        obtainEvent(ProfileEvent.OnLoadProfile)
    }

    override fun obtainEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLoadProfile -> loadProfile(showLoading = true)
            is ProfileEvent.OnRefreshProfile -> loadProfile(showLoading = false)
            is ProfileEvent.OnLogoutClick -> onLogoutClick()
            is ProfileEvent.OnEditClick -> onEditClick()
        }
    }

    private fun loadProfile(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                viewState = ProfileViewState.Loading
            }
            
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

    private fun onEditClick() {
        (viewState as? ProfileViewState.Content)?.let { content ->
            viewAction = ProfileAction.NavigateToEdit(content.profile)
        }
    }

    private fun onLogoutClick() {
        (viewState as? ProfileViewState.Content)?.let { content ->
            viewState = content.copy(isLoggingOut = true)
            viewAction = ProfileAction.NavigateToLogin
        }
    }
}
