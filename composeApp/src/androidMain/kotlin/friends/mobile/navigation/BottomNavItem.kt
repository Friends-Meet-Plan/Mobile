package friends.mobile.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

internal sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Friends : BottomNavItem("friends", "Friends", Icons.Default.People)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
    object Archive : BottomNavItem("archive", "Archive", Icons.Default.CheckBox)
}
