package friends.mobile.feature.friends.data.mapper

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.model.User

internal fun UserDto.toDomain(): User =
    User(
        id = id,
        username = username,
        bio = bio,
        avatarUrl = avatarUrl,
    )

internal fun List<UserDto>.toDomain(): List<User> =
    map { it.toDomain() }
