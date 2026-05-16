package friends.mobile.main

import android.util.Log
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
import androidx.navigation.toRoute
import friends.mobile.events.CreateEventView
import friends.mobile.events.EventDetailView
import friends.mobile.friends.FriendsScreen
import friends.mobile.profile.EditProfileScreen
import friends.mobile.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable data object Home : Screen
    @Serializable data class EventDetail(val eventId: String) : Screen
    @Serializable data class CreateEvent(val date: String) : Screen
    @Serializable data object Friends : Screen
    @Serializable data object Profile : Screen
    @Serializable data class ProfileEdit(
        val username: String,
        val bio: String?,
        val avatarUrl: String?
    ) : Screen
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        Triple(Screen.Home, "Home", BottomNavItem.Home.icon),
        Triple(Screen.Friends, "Friends", BottomNavItem.Friends.icon),
        Triple(Screen.Profile, "Profile", BottomNavItem.Profile.icon)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { (screen, title, icon) ->
                    val routeName = screen::class.qualifiedName ?: ""
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = currentDestination?.hierarchy?.any { it.route?.contains(routeName) == true } == true,
                        onClick = {
                            navController.navigate(screen) {
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
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                MainView(
                    onEventDetailClick = { eventId ->
                        navController.navigate(Screen.EventDetail(eventId))
                    },
                    onCreateEventClick = { date ->
                        navController.navigate(Screen.CreateEvent(date))
                    }
                )
            }
            composable<Screen.EventDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.EventDetail>()
                EventDetailView(
                    eventId = args.eventId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<Screen.CreateEvent> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.CreateEvent>()
                CreateEventView(
                    selectedDate = args.date,
                    onEventCreated = { eventId ->
                        navController.navigate(Screen.EventDetail(eventId)) {
                            popUpTo(Screen.CreateEvent(args.date)) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<Screen.Friends> {
                FriendsScreen()
            }
            composable<Screen.Profile> {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditClick = { profile ->
                        navController.navigate(
                            Screen.ProfileEdit(profile.username, profile.bio, profile.avatarUrl)
                        )
                    }
                )
            }
            composable<Screen.ProfileEdit> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.ProfileEdit>()
                EditProfileScreen(
                    initialUsername = args.username,
                    initialBio = args.bio ?: "",
                    initialAvatarUrl = args.avatarUrl ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
