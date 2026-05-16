package friends.mobile.feature.wishplaces.data.mapper

import friends.mobile.feature.wishplaces.data.remote.dto.WishPlaceDto
import friends.mobile.feature.wishplaces.domain.model.WishPlace
import friends.mobile.feature.wishplaces.domain.model.WishPlaceStatus

internal class WishPlaceMapper {
    fun map(dto: WishPlaceDto): WishPlace {
        return WishPlace(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            location = dto.location,
            link = dto.link,
            status = WishPlaceStatus.fromString(dto.status),
            createdAt = dto.createdAt,
            visitedEventId = dto.visitedEventId
        )
    }

    fun map(dtos: List<WishPlaceDto>): List<WishPlace> {
        return dtos.map { map(it) }
    }
}
