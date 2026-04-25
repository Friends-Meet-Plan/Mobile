package friends.mobile.auth.presentation

import friends.mobile.auth.repository.AuthRepository
import friends.mobile.network.NetworkException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterViewModel : KoinComponent {
    private val repository: AuthRepository by inject()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun register(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (username.isBlank()) {
            onError("Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            onError("Password cannot be empty")
            return
        }
        scope.launch {
            try {
                repository.register(username, password)
                onSuccess()
            } catch (e: NetworkException.Conflict) {
                onError("Username is already taken")
            } catch (e: NetworkException.NetworkError) {
                onError("Network error, check your connection")
            } catch (e: Exception) {
                onError("Something went wrong")
            }
        }
    }

    fun dispose() = scope.cancel()
}
