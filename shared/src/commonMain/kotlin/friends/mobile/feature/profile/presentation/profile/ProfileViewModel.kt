package friends.mobile.feature.profile.presentation.profile

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.profile.domain.usecase.GetMeUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ProfileViewModel : BaseViewModel<ProfileViewState, ProfileAction, ProfileEvent>(
    initState = ProfileViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_PROFILE,
) {

    private val getMeUseCase: GetMeUseCase by inject()
    private var isLoadingInProgress = false

    init {
        obtainEvent(ProfileEvent.OnLoadProfile)
    }

    override fun obtainEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLoadProfile -> loadProfile(showLoading = true)
            is ProfileEvent.OnRefreshProfile -> onRefresh()
            is ProfileEvent.OnLogoutClick -> onLogoutClick()
            is ProfileEvent.OnEditClick -> onEditClick()
        }
    }

    private fun loadProfile(showLoading: Boolean) {
        viewModelScope.launch {
            if (isLoadingInProgress) return@launch

            isLoadingInProgress = true

            try {
                if (showLoading) {
                    viewState = ProfileViewState.Loading
                } else {
                    val currentState = viewState
                    if (currentState is ProfileViewState.Content) {
                        viewState = currentState.copy(isRefreshing = true)
                    }
                }

                when (val result = getMeUseCase()) {
                    is ResultWrapper.Success -> {
                        viewState = ProfileViewState.Content(
                            profile = result.data,
                            isRefreshing = false
                        )
                    }
                    is ResultWrapper.Error -> {
                        val currentState = viewState
                        val userError = mapApiErrorToUserFriendly(result.error)

                        if (currentState is ProfileViewState.Content) {
                            viewState = currentState.copy(isRefreshing = false)
                        } else {
                            viewState = ProfileViewState.Error(
                                message = getErrorMessage(userError)
                            )
                        }
                    }
                }
            } finally {
                isLoadingInProgress = false
            }
        }
    }

    private fun onRefresh() {
        if (isLoadingInProgress) return

        val currentState = viewState
        if (currentState is ProfileViewState.Content) {
            viewState = currentState.copy(isRefreshing = true)
        }

        loadProfile(showLoading = false)
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
