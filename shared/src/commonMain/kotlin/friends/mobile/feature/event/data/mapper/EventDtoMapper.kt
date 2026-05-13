package friends.mobile.feature.event.data.mapper

import friends.mobile.feature.event.data.remote.dto.EventResponseDto
import friends.mobile.feature.event.data.remote.dto.ParticipantDto
import friends.mobile.feature.event.domain.model.Event
import friends.mobile.feature.event.domain.model.EventParticipant
import friends.mobile.feature.event.domain.model.EventStatus
import friends.mobile.feature.event.domain.model.ParticipantResponseStatus
import friends.mobile.feature.event.domain.model.ParticipantRole

/**
 * Mapper for converting DTOs to domain models.
 */
class EventDtoMapper {

    fun map(dto: EventResponseDto): Event {
        return Event(
            id = dto.id,
            creatorId = dto.creatorId,
            date = dto.date,
            title = dto.title,
            description = dto.description,
            location = dto.location,
            status = EventStatus.fromString(dto.status),
            wishPlaceId = dto.wishPlaceId,
            memoryImageBase64 = dto.memoryImageBase64,
            createdAt = dto.createdAt,
            participants = dto.participants.map { mapParticipant(it) },
        )
    }

    private fun mapParticipant(dto: ParticipantDto): EventParticipant {
        return EventParticipant(
            userId = dto.userId,
            role = ParticipantRole.fromString(dto.role),
            responseStatus = ParticipantResponseStatus.fromString(dto.responseStatus),
        )
    }
}
