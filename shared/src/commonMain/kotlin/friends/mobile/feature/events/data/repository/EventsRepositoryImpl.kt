package friends.mobile.feature.events.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.events.data.mapper.EventDtoMapper
import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.events.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.domain.model.User

internal class EventsRepositoryImpl(
    private val api: EventsApi,
    private val eventDtoMapper: EventDtoMapper,
    private val userDtoMapper: UserDtoMapper,
) : EventsRepository {

    override suspend fun checkFriendsAvailability(date: String): ResultWrapper<List<User>> =
        safeApiCall {
            val response = api.checkFriendsAvailability(date)
            userDtoMapper.toDomain(response.availableFriends)
        }

    override suspend fun createEvent(
        title: String,
        description: String?,
        date: String,
        time: String?,
        location: String?,
        invitedFriendIds: List<String>,
    ): ResultWrapper<Event> = safeApiCall {
        val body = CreateEventRequestDto(
            title = title,
            description = description,
            date = date,
            time = time,
            location = location,
            invitedFriendIds = invitedFriendIds,
        )
        val response = api.createEvent(body)
        eventDtoMapper.toDomain(response)
    }
}
