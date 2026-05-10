package friends.mobile.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.auth.presentation.RootEvent
import friends.mobile.feature.auth.presentation.RootViewModel
import friends.mobile.feature.auth.presentation.RootViewState
import friends.mobile.main.MainScreen

@Composable
fun RootScreen() {
    val viewModel: RootViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val session = (state as? RootViewState.Content)?.session

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
