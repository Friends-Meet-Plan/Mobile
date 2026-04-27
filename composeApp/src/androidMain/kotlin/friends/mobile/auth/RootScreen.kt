package friends.mobile.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import friends.mobile.feature.auth.presentation.OnSessionStarted
import friends.mobile.feature.auth.presentation.RootViewModel as SharedRootViewModel

@Composable
fun RootScreen() {
    val viewModel: SharedRootViewModel = viewModel()
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val session = state.session

    if (session == null) {
        LoginScreen(
            onLoginSuccess = { authSession ->
                viewModel.obtainEvent(
                    friends.mobile.feature.auth.presentation.OnSessionStarted(
                        authSession
                    )
                )
            },
        )
    } else {
        MainScreen(username = session.user.username)
    }
}

@Composable
private fun MainScreen(username: String) {
    Text("Main, $username")
}
