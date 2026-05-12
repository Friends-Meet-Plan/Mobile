package friends.mobile.feature.profile.data.remote

import friends.mobile.feature.profile.data.remote.dto.ProfileResponseDto
import friends.mobile.feature.profile.data.remote.dto.UpdateProfileRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody

internal class ProfileApi(
    private val client: HttpClient,
) {
    suspend fun getMe(): ProfileResponseDto =
        client.get("/users/me").body()

    suspend fun patchMe(body: UpdateProfileRequestDto): ProfileResponseDto =
        client.patch("/users/me") {
            setBody(body)
        }.body()
}
