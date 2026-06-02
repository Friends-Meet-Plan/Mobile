package friends.mobile.feature.profile.presentation.edit

import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class EditProfileViewModel : BaseViewModel<EditProfileViewState, EditProfileAction, EditProfileEvent>(
    initState = EditProfileViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_EDIT_PROFILE,
) {

    private val updateProfileUseCase: UpdateProfileUseCase by inject()

    override fun obtainEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.Init -> onInit(event)
            is EditProfileEvent.OnUsernameChanged -> updateContent { it.copy(username = event.value) }
            is EditProfileEvent.OnBioChanged -> updateContent { it.copy(bio = event.value) }
            is EditProfileEvent.OnAvatarUrlChanged -> updateContent { it.copy(avatarUrl = event.value) }
            is EditProfileEvent.OnSaveClick -> onSaveClick()
            is EditProfileEvent.OnBackClick -> viewAction = EditProfileAction.NavigateBack
            is EditProfileEvent.OnRetryClick -> viewAction = EditProfileAction.NavigateBack
        }
    }

    private fun onInit(event: EditProfileEvent.Init) {
        viewState = EditProfileViewState.Content(
            username = event.username,
            bio = event.bio,
            avatarUrl = event.avatarUrl
        )
    }

    private fun onSaveClick() {
        val currentState = viewState as? EditProfileViewState.Content ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isSaving = true)
            val result = updateProfileUseCase(
                username = currentState.username,
                bio = currentState.bio,
                avatarUrl = currentState.avatarUrl
            )
            when (result) {
                is ResultWrapper.Success -> {
                    viewAction = EditProfileAction.NavigateBack
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = EditProfileViewState.Error(
                        message = getErrorMessage(userError)
                    )
                }
            }
        }
    }

    private fun updateContent(transform: (EditProfileViewState.Content) -> EditProfileViewState.Content) {
        (viewState as? EditProfileViewState.Content)?.let {
            viewState = transform(it)
        }
    }
}
