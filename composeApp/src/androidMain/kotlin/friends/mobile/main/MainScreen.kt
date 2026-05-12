package friends.mobile.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import friends.mobile.friends.FriendsScreen
import friends.mobile.profile.EditProfileScreen
import friends.mobile.profile.ProfileScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Friends,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                PlaceholderScreen("Home Screen")
            }
            composable(BottomNavItem.Friends.route) {
                FriendsScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditClick = { profile ->
                        // Переходим на экран редактирования и передаем данные
                        // Для простоты используем навигацию с аргументами или без,
                        // если ViewModel будет подтягивать данные сама, но здесь передадим через аргументы
                        navController.navigate(
                            "profile_edit/${profile.username}/${profile.bio ?: " "}/${profile.avatarUrl ?: " "}"
                        )
                    }
                )
            }
            // Редактирование профиля (вне нижнего бара, но внутри NavHost)
            composable(
                route = "profile_edit/{username}/{bio}/{avatarUrl}"
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val bio = backStackEntry.arguments?.getString("bio") ?: ""
                val avatarUrl = backStackEntry.arguments?.getString("avatarUrl") ?: ""

                EditProfileScreen(
                    initialUsername = username,
                    initialBio = if (bio == " ") "" else bio,
                    initialAvatarUrl = if (avatarUrl == " ") "" else avatarUrl,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}
