package friends.mobile.feature.events.data.mapper

import friends.mobile.feature.events.data.remote.dto.EventParticipantDto
import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.events.domain.model.EventParticipant
import friends.mobile.feature.events.domain.model.ParticipationStatus

internal class EventDtoMapper {

    fun toDomain(dto: EventResponseDto): Event =
        Event(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            date = dto.date,
            time = dto.time,
            location = dto.location,
            creatorId = dto.creatorId,
            participants = dto.participants.map { toDomain(it) },
        )

    private fun toDomain(dto: EventParticipantDto): EventParticipant =
        EventParticipant(
            userId = dto.userId,
            username = dto.username,
            avatarUrl = dto.avatarUrl,
            status = ParticipationStatus.valueOf(dto.status.uppercase()),
        )
}
