package friends.mobile.feature.auth.data.mapper

import friends.mobile.feature.auth.data.remote.dto.LoginResponseDto
import friends.mobile.feature.auth.data.remote.dto.RefreshResponseDto
import friends.mobile.feature.auth.domain.model.AuthSession
import friends.mobile.feature.auth.domain.model.AuthToken
import friends.mobile.feature.auth.domain.model.AuthUser

internal class AuthSessionMapper {
    fun loginResponseToDomain(response: LoginResponseDto): AuthSession {
        return AuthSession(
            token = AuthToken(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            ),
            user = AuthUser(
                id = response.user.id,
                username = response.user.username,
                avatarUrl = response.user.avatarUrl,
                bio = response.user.bio,
            ),
        )
    }

    fun refreshResponseToDomainToken(response: RefreshResponseDto): AuthToken {
        return AuthToken(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
    }
}
