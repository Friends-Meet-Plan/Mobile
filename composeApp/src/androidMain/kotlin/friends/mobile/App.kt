package friends.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import friends.mobile.auth.RootScreen
import friends.mobile.designkit.theme.FriendsAppTheme

@Composable
@Preview
fun App() {
    FriendsAppTheme {
        RootScreen()
    }
}
