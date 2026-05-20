package friends.mobile.feature.main.data.mapper

import friends.mobile.feature.events.data.remote.dto.EventResponseDto
import friends.mobile.feature.main.data.remote.dto.EventListItemDto
import friends.mobile.feature.main.domain.model.MainEvent

internal class MainEventMapper {

    fun toDomain(dto: EventListItemDto): MainEvent {
        return MainEvent(
            id = dto.id,
            title = dto.title,
            date = dto.date,
            time = "12:00",
            creatorId = dto.creatorId,
            participantCount = dto.participants.size,
        )
    }

    fun toDomain(dtos: List<EventListItemDto>): List<MainEvent> {
        return dtos.map { toDomain(it) }
    }

    fun toDomainFromEventResponse(dto: EventResponseDto): MainEvent {
        return MainEvent(
            id = dto.id,
            title = dto.title,
            date = dto.date,
            time = dto.time ?: "12:00",
            creatorId = dto.creatorId,
            participantCount = dto.participants.size,
        )
    }

    fun toDomainFromEventResponse(dtos: List<EventResponseDto>): List<MainEvent> {
        return dtos.map { toDomainFromEventResponse(it) }
    }
}
