package friends.mobile.feature.auth.presentation.register

sealed class RegisterViewState {
    data object Loading : RegisterViewState()
    data class Error(val message: String) : RegisterViewState()
    data class Content(
        val hint: String? = null,
    ) : RegisterViewState()
}
