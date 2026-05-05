package friends.mobile.feature.auth.data.remote

import friends.mobile.feature.auth.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AuthApi(
    private val client: HttpClient,
) {

    suspend fun register(request: RegisterRequestDto) {
        client.post("/auth/register") {
            setBody(request)
        }
    }

    suspend fun login(request: LoginRequestDto): LoginResponseDto =
        client.post("/auth/login") {
            setBody(request)
        }.body()

    suspend fun refresh(request: RefreshRequestDto): RefreshResponseDto =
        client.post("/auth/refresh") {
            setBody(request)
        }.body()

    suspend fun logout(request: LogoutRequestDto) {
        client.post("/auth/logout") {
            setBody(request)
        }
    }
}