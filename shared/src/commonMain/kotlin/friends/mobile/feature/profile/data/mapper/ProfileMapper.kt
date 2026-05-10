package friends.mobile.feature.profile.data.mapper

import friends.mobile.feature.profile.data.remote.dto.ProfileResponseDto
import friends.mobile.feature.profile.domain.model.Profile

internal class ProfileMapper {
    fun mapToDomain(dto: ProfileResponseDto): Profile = Profile(
        id = dto.id,
        username = dto.username,
        avatarUrl = dto.avatarUrl,
        bio = dto.bio
    )
}
