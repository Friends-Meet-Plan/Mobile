package friends.mobile.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import friends.mobile.feature.auth.presentation.RootEvent
import friends.mobile.feature.auth.presentation.RootViewModel
import friends.mobile.feature.auth.presentation.RootViewState
import friends.mobile.main.MainScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootScreen(
    viewModel: RootViewModel = koinViewModel()
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is RootViewState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RootViewState.Error -> {
            // Здесь можно добавить экран глобальной ошибки
        }
        is RootViewState.Content -> {
            val session = currentState.session
            if (session == null) {
                LoginScreen(
                    onLoginSuccess = { authSession ->
                        viewModel.obtainEvent(RootEvent.OnSessionStarted(authSession))
                    },
                )
            } else {
                MainScreen(
                    onLogout = {
                        viewModel.obtainEvent(RootEvent.OnLogoutClick)
                    }
                )
            }
        }
    }
}
