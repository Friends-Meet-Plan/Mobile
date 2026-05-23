package friends.mobile.feature.events.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.eventdetail.domain.model.EventDetail
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
    ): ResultWrapper<String>

    suspend fun getWaitingEvents(): ResultWrapper<List<Event>>

    suspend fun getEventDetail(eventId: String): ResultWrapper<EventDetail>

    suspend fun acceptEvent(eventId: String): ResultWrapper<Unit>

    suspend fun declineEvent(eventId: String): ResultWrapper<Unit>
}
