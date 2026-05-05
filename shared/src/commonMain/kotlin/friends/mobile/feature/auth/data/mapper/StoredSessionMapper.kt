package friends.mobile.feature.auth.data.mapper

import friends.mobile.feature.auth.data.storage.dto.StoredSessionDto
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.model.AuthUser

internal class StoredSessionMapper {
    fun storedSessionDtoToDomain(dto: StoredSessionDto): AuthSession {
        return AuthSession(
            token = AuthToken(dto.accessToken, dto.refreshToken),
            user = AuthUser(dto.userId, dto.username, dto.avatarUrl, dto.bio),
        )
    }

    fun domainSessionToStoredSessionDto(session: AuthSession): StoredSessionDto {
        return StoredSessionDto(
            accessToken = session.token.accessToken,
            refreshToken = session.token.refreshToken,
            userId = session.user.id,
            username = session.user.username,
            avatarUrl = session.user.avatarUrl,
            bio = session.user.bio,
        )
    }
}

