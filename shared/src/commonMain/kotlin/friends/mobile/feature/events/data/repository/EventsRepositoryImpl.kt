package friends.mobile.feature.events.data.repository

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.network.safeApiCall
import friends.mobile.feature.events.data.remote.EventsApi
import friends.mobile.feature.events.data.remote.dto.CreateEventRequestDto
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.EventParticipant
import friends.mobile.feature.events.domain.model.ParticipationStatus
import friends.mobile.feature.events.domain.repository.EventsRepository
import friends.mobile.feature.friends.data.mapper.UserDtoMapper
import friends.mobile.feature.friends.domain.model.User

internal class EventsRepositoryImpl(
    private val api: EventsApi,
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
    ): ResultWrapper<String> = safeApiCall {
        val body = CreateEventRequestDto(
            title = title,
            description = description,
            date = date,
            time = time,
            location = location,
            invitedFriendIds = invitedFriendIds,
        )
        val response = api.createEvent(body)
        response.id
    }

    override suspend fun getPendingEvents(): ResultWrapper<List<Event>> = safeApiCall {
        val response = api.getPendingEvents()
        response.map { dto ->
            Event(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                date = dto.date,
                time = dto.time,
                location = dto.location,
                creatorId = dto.creatorId,
                participants = dto.participants.map { participantDto ->
                    EventParticipant(
                        userId = participantDto.userId,
                        username = participantDto.username,
                        avatarUrl = participantDto.avatarUrl,
                        status = parseParticipationStatus(participantDto.status),
                    )
                },
            )
        }
    }

    override suspend fun acceptEvent(eventId: String): ResultWrapper<Unit> = safeApiCall {
        api.acceptEvent(eventId)
    }

    override suspend fun declineEvent(eventId: String): ResultWrapper<Unit> = safeApiCall {
        api.declineEvent(eventId)
    }

    private fun parseParticipationStatus(status: String): ParticipationStatus {
        return when (status.uppercase()) {
            "ACCEPTED" -> ParticipationStatus.ACCEPTED
            "DECLINED" -> ParticipationStatus.DECLINED
            "INVITED" -> ParticipationStatus.INVITED
            else -> ParticipationStatus.INVITED
        }
    }
}
