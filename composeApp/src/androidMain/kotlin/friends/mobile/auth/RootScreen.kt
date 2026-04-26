package friends.mobile.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import friends.mobile.auth.model.AuthSession

@Composable
fun RootScreen() {
    var session by remember { mutableStateOf<AuthSession?>(null) }

    if (session == null) {
        LoginScreen(
            onLoginSuccess = { authSession ->
                session = authSession
            },
        )
    } else {
        MainScreen()
    }
}

@Composable
private fun MainScreen() {
    Text("Main")
}
