package friends.mobile.feature.profile.data.remote

import friends.mobile.feature.profile.data.remote.dto.ProfileResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class ProfileApi(
    private val client: HttpClient,
) {
    suspend fun getMe(): ProfileResponseDto =
        client.get("/users/me").body()
}
