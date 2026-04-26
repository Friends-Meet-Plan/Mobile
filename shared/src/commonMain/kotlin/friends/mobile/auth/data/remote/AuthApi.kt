package friends.mobile.auth.remote

import friends.mobile.network.NetworkException
import friends.mobile.auth.remote.dto.LoginRequestDto
import friends.mobile.auth.remote.dto.LoginResponseDto
import friends.mobile.auth.remote.dto.LogoutRequestDto
import friends.mobile.auth.remote.dto.RefreshRequestDto
import friends.mobile.auth.remote.dto.RefreshResponseDto
import friends.mobile.auth.remote.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/** Ktor wrapper for all auth endpoints. Maps HTTP errors to [NetworkException]. */
class AuthApi(private val client: HttpClient) {

    suspend fun register(request: RegisterRequestDto) {
        execute { client.post("/auth/register") { contentType(ContentType.Application.Json); setBody(request) } }
    }

    suspend fun login(request: LoginRequestDto): LoginResponseDto =
        execute { client.post("/auth/login") { contentType(ContentType.Application.Json); setBody(request) } }.body()

    suspend fun refresh(request: RefreshRequestDto): RefreshResponseDto =
        execute { client.post("/auth/refresh") { contentType(ContentType.Application.Json); setBody(request) } }.body()

    suspend fun logout(request: LogoutRequestDto) {
        execute { client.post("/auth/logout") { contentType(ContentType.Application.Json); setBody(request) } }
    }

    private suspend fun execute(block: suspend () -> HttpResponse): HttpResponse {
        return try {
            val response = block()
            if (response.status.isSuccess()) response else throw mapStatus(response.status)
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException.NetworkError(e)
        }
    }

    private fun mapStatus(status: HttpStatusCode): NetworkException = when (status) {
        HttpStatusCode.Unauthorized -> NetworkException.InvalidCredentials
        HttpStatusCode.Conflict     -> NetworkException.Conflict
        else                        -> NetworkException.UnknownError(status.value)
    }
}
