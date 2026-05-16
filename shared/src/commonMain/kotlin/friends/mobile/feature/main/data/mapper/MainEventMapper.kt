package friends.mobile.feature.main.data.mapper

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
}
