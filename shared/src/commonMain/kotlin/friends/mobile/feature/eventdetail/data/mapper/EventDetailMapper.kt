package friends.mobile.feature.eventdetail.data.mapper

import friends.mobile.feature.eventdetail.domain.model.EventDetail
import friends.mobile.feature.eventdetail.domain.model.EventParticipant
import friends.mobile.feature.events.data.remote.dto.EventParticipantDto
import friends.mobile.feature.events.data.remote.dto.EventResponseDto

internal class EventDetailMapper {

    fun toDomain(dto: EventResponseDto): EventDetail =
        EventDetail(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            date = dto.date,
            time = dto.time,
            location = dto.location,
            creatorId = dto.creatorId,
            status = dto.status,
            participants = dto.participants.map { toDomain(it) },
        )

    private fun toDomain(dto: EventParticipantDto): EventParticipant =
        EventParticipant(
            userId = dto.userId,
            username = dto.username,
            avatarUrl = dto.avatarUrl,
            bio = dto.bio,
            role = dto.role,
            responseStatus = dto.status,
        )
}
