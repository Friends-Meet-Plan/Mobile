package friends.mobile.navigation

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

    @Serializable data object PendingEvents : Screen

    @Serializable data object Archive : Screen
}
