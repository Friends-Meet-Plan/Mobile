package friends.mobile.feature.profile.presentation

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
            try {
                val profile = getMeUseCase()
                viewState = viewState.copy(profile = profile, isLoading = false)
            } catch (_: Exception) {
                viewState = viewState.copy(isLoading = false, error = "Unknown error")
            }
        }
    }

    private fun onLogoutClick() {
        viewAction = ProfileAction.LogoutRequested
    }
}
