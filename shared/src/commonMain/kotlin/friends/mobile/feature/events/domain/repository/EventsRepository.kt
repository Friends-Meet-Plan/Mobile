package friends.mobile.feature.events.domain.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.friends.domain.model.User

interface EventsRepository {
    suspend fun checkFriendsAvailability(date: String): ResultWrapper<List<User>>

    suspend fun checkUserAvailability(date: String): ResultWrapper<Boolean>

    suspend fun createEvent(
        title: String,
        description: String?,
        date: String,
        time: String?,
        location: String?,
        invitedFriendIds: List<String>,
    ): ResultWrapper<String>

    suspend fun getWaitingEvents(): ResultWrapper<List<Event>>
    suspend fun getAcceptedEvents(): ResultWrapper<List<Event>>
    suspend fun getArchivedEvents(): ResultWrapper<List<Event>>

    suspend fun getEventDetail(eventId: String): ResultWrapper<Event>

    suspend fun acceptEvent(eventId: String): ResultWrapper<Unit>

    suspend fun declineEvent(eventId: String): ResultWrapper<Unit>
}
