package friends.mobile.feature.events.data.mapper

import friends.mobile.feature.events.data.remote.dto.EventParticipantDto
import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.EventParticipant
import friends.mobile.feature.events.domain.model.ParticipationStatus

internal class EventMapper {

    fun mapEvents(dtos: List<EventResponseDto>): List<Event> = dtos.map { mapEvent(it) }

    fun mapEvent(dto: EventResponseDto): Event {
        return Event(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            date = dto.date,
            time = dto.time,
            location = dto.location,
            creatorId = dto.creatorId,
            status = dto.status,
            participants = dto.participants.map { mapParticipant(it) }
        )
    }

    private fun mapParticipant(dto: EventParticipantDto): EventParticipant {
        return EventParticipant(
            userId = dto.userId,
            username = dto.username,
            avatarUrl = dto.avatarUrl,
            bio = dto.bio,
            role = dto.role,
            status = parseParticipationStatus(dto.status)
        )
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
