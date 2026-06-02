package friends.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import friends.mobile.designsystem.theme.FriendsAppTheme
import friends.mobile.feature.auth.RootScreen

@Composable
@Preview
fun App() {
    FriendsAppTheme {
        RootScreen()
    }
}
