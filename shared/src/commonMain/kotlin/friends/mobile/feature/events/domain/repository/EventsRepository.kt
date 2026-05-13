package friends.mobile.feature.events.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.friends.domain.model.User

interface EventsRepository {
    suspend fun checkFriendsAvailability(date: String): ResultWrapper<List<User>>

    suspend fun createEvent(
        title: String,
        description: String?,
        date: String,
        time: String?,
        location: String?,
        invitedFriendIds: List<String>,
    ): ResultWrapper<Event>
}
