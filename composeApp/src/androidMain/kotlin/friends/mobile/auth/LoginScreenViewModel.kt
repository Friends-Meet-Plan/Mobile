package friends.mobile.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import friends.mobile.auth.model.AuthSession
import friends.mobile.auth.presentation.LoginViewModel as KmpLoginViewModel

class LoginScreenViewModel : ViewModel() {

    private val kmpViewModel = KmpLoginViewModel()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(
        username: String,
        password: String,
        onSuccess: (AuthSession) -> Unit,
    ) {
        isLoading = true
        errorMessage = null
        kmpViewModel.login(
            username = username,
            password = password,
            onSuccess = { session ->
                isLoading = false
                onSuccess(session)
            },
            onError = { message ->
                isLoading = false
                errorMessage = message
            },
        )
    }

    override fun onCleared() {
        super.onCleared()
        kmpViewModel.dispose()
    }
}
