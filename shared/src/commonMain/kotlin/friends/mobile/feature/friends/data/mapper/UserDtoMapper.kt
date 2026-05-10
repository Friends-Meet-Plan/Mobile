package friends.mobile.feature.friends.data.mapper

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.model.User

internal class UserDtoMapper {
    fun toDomain(dto: UserDto): User =
        User(
            id = dto.id,
            username = dto.username,
            bio = dto.bio,
            avatarUrl = dto.avatarUrl,
        )

    fun toDomain(dtos: List<UserDto>): List<User> =
        dtos.map { toDomain(it) }
}
