package friends.mobile.auth.presentation

import friends.mobile.auth.model.AuthSession
import friends.mobile.auth.repository.AuthRepository
import friends.mobile.network.NetworkException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : KoinComponent {
    private val repository: AuthRepository by inject()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun login(
        username: String,
        password: String,
        onSuccess: (AuthSession) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            try {
                val session = repository.login(username, password)
                onSuccess(session)
            } catch (e: NetworkException.InvalidCredentials) {
                onError("Wrong username or password")
            } catch (e: NetworkException.NetworkError) {
                onError("Network error, check your connection")
            } catch (e: Exception) {
                onError("Something went wrong")
            }
        }
    }

    fun dispose() = scope.cancel()
}
