package friends.mobile.feature.profile.presentation.edit

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditProfileViewModel :
    BaseViewModel<EditProfileViewState, EditProfileAction, EditProfileEvent>(
        initState = EditProfileViewState.Loading,
    ),
    KoinComponent {

    private val updateProfileUseCase: UpdateProfileUseCase by inject()

    override fun obtainEvent(event: EditProfileEvent) {
        when (event) {
            // Передаем event как конкретный тип EditProfileEvent.Init
            is EditProfileEvent.Init -> onInit(event)

            is EditProfileEvent.OnUsernameChanged -> updateContent { it.copy(username = event.value) }
            is EditProfileEvent.OnBioChanged -> updateContent { it.copy(bio = event.value) }
            is EditProfileEvent.OnAvatarUrlChanged -> updateContent { it.copy(avatarUrl = event.value) }
            is EditProfileEvent.OnSaveClick -> onSaveClick()
            is EditProfileEvent.OnBackClick -> viewAction = EditProfileAction.NavigateBack
        }
    }

    // Указываем конкретный тип EditProfileEvent.Init здесь
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
            viewState = currentState.copy(isSaving = true, saveError = null)

            val result = updateProfileUseCase(
                username = currentState.username,
                bio = currentState.bio,
                avatarUrl = currentState.avatarUrl
            )

            when (result) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isSaving = false)
                    viewAction = EditProfileAction.NavigateBack
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isSaving = false,
                        saveError = getErrorMessage(userError)
                    )
                }
            }
        }
    }

    private fun updateContent(transform: (EditProfileViewState.Content) -> EditProfileViewState.Content) {
        val currentState = viewState as? EditProfileViewState.Content
        if (currentState != null) {
            viewState = transform(currentState)
        }
    }
}
