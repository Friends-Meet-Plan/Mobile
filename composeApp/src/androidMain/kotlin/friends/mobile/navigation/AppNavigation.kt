package friends.mobile.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import friends.mobile.designsystem.theme.DesignTheme
import friends.mobile.feature.archive.ArchiveEventsView
import friends.mobile.feature.events.CreateEventView
import friends.mobile.feature.events.EventDetailView
import friends.mobile.feature.events.PendingEventListView
import friends.mobile.feature.friends.FriendsScreen
import friends.mobile.feature.main.MainView
import friends.mobile.feature.profile.EditProfileScreen
import friends.mobile.feature.profile.ProfileScreen

@Composable
fun AppNavigation(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        Triple(Screen.Home, "Main", BottomNavItem.Home.icon),
        Triple(Screen.Friends, "Friends", BottomNavItem.Friends.icon),
        Triple(Screen.Archive, "Archive", BottomNavItem.Archive.icon),
        Triple(Screen.Profile, "Profile", BottomNavItem.Profile.icon)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { (screen, _, _) ->
        val routeName = screen::class.qualifiedName ?: ""
        currentDestination?.hierarchy?.any { it.route?.contains(routeName) == true } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    bottomNavItems.forEach { (screen, title, icon) ->
                        val routeName = screen::class.qualifiedName ?: ""
                        val selected = currentDestination?.hierarchy?.any {
                            it.route?.contains(routeName) == true
                        } == true

                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = title) },
                            label = {
                                Text(
                                    text = title,
                                    style = DesignTheme.Typography.bodySmallest
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                unselectedIconColor = Color.Black,
                                unselectedTextColor = Color.Black,
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val tabRoutes = remember {
            setOf(
                Screen.Home::class.qualifiedName ?: "",
                Screen.Friends::class.qualifiedName ?: "",
                Screen.Archive::class.qualifiedName ?: "",
                Screen.Profile::class.qualifiedName ?: "",
            ).filter { it.isNotEmpty() }.toSet()
        }

        fun isTab(route: String?) = tabRoutes.any { route?.contains(it) == true }

        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                if (isTab(initialState.destination.route) && isTab(targetState.destination.route)) {
                    fadeIn(tween(200))
                } else {
                    slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it }
                }
            },
            exitTransition = {
                if (isTab(initialState.destination.route) && isTab(targetState.destination.route)) {
                    fadeOut(tween(200))
                } else {
                    slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 4 }
                }
            },
            popEnterTransition = {
                if (isTab(initialState.destination.route) && isTab(targetState.destination.route)) {
                    fadeIn(tween(200))
                } else {
                    slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 4 }
                }
            },
            popExitTransition = {
                if (isTab(initialState.destination.route) && isTab(targetState.destination.route)) {
                    fadeOut(tween(200))
                } else {
                    slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it }
                }
            }
        ) {
            composable<Screen.Home> {
                MainView(
                    onEventDetailClick = { eventId ->
                        navController.navigate(Screen.EventDetail(eventId))
                    },
                    onCreateEventClick = { date ->
                        navController.navigate(Screen.CreateEvent(date))
                    },
                    onPendingEventsClick = {
                        navController.navigate(Screen.PendingEvents)
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
            composable<Screen.PendingEvents> {
                PendingEventListView(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<Screen.Archive> {
                ArchiveEventsView(
                    onEventDetailClick = { eventId ->
                        navController.navigate(Screen.EventDetail(eventId))
                    }
                )
            }
        }
    }
}
