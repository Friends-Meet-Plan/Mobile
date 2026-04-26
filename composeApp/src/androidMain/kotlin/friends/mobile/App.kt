package friends.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import friends.mobile.auth.RootScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        RootScreen()
    }
}
