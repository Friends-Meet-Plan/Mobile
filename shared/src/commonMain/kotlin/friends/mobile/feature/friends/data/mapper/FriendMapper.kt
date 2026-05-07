package friends.mobile.feature.friends.data.mapper

import friends.mobile.feature.friends.data.remote.dto.FriendDto
import friends.mobile.feature.friends.domain.model.Friend

/**
 * Maps between API DTOs (FriendDto) and domain models (Friend).
 */
class FriendMapper {

    fun mapDtoToDomain(dto: FriendDto): Friend = Friend(
        id = dto.id,
        username = dto.username,
        avatarUrl = dto.avatarUrl,
        bio = dto.bio,
    )

    fun mapDtoListToDomain(dtos: List<FriendDto>): List<Friend> =
        dtos.map { mapDtoToDomain(it) }
}
