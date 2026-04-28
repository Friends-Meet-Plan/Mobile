package friends.mobile.feature.auth.presentation

data class LoginViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
