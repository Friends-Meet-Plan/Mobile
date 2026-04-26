package friends.mobile.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import friends.mobile.auth.presentation.RegisterViewModel as KmpRegisterViewModel

class RegisterScreenViewModel : ViewModel() {

    private val kmpViewModel = KmpRegisterViewModel()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun register(
        username: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        isLoading = true
        errorMessage = null
        kmpViewModel.register(
            username = username,
            password = password,
            onSuccess = {
                isLoading = false
                onSuccess()
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
