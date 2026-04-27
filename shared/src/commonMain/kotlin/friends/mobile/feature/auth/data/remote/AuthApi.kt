package friends.mobile.feature.auth.data.remote

import friends.mobile.feature.auth.data.remote.dto.LoginRequestDto
import friends.mobile.feature.auth.data.remote.dto.LoginResponseDto
import friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto
import friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto
import friends.mobile.feature.auth.data.remote.dto.RefreshResponseDto
import friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto
import friends.mobile.core.network.NetworkException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/** Ktor wrapper for all auth endpoints. Maps HTTP errors to [NetworkException]. */
class AuthApi(private val client: HttpClient) {

    suspend fun register(request: friends.mobile.feature.auth.data.remote.dto.RegisterRequestDto) {
        execute { client.post("/auth/register") { contentType(ContentType.Application.Json); setBody(request) } }
    }

    suspend fun login(request: friends.mobile.feature.auth.data.remote.dto.LoginRequestDto): friends.mobile.feature.auth.data.remote.dto.LoginResponseDto =
        execute { client.post("/auth/login") { contentType(ContentType.Application.Json); setBody(request) } }.body()

    suspend fun refresh(request: friends.mobile.feature.auth.data.remote.dto.RefreshRequestDto): friends.mobile.feature.auth.data.remote.dto.RefreshResponseDto =
        execute { client.post("/auth/refresh") { contentType(ContentType.Application.Json); setBody(request) } }.body()

    suspend fun logout(request: friends.mobile.feature.auth.data.remote.dto.LogoutRequestDto) {
        execute { client.post("/auth/logout") { contentType(ContentType.Application.Json); setBody(request) } }
    }

    private suspend fun execute(block: suspend () -> HttpResponse): HttpResponse {
        return try {
            val response = block()
            if (response.status.isSuccess()) response else throw mapStatus(response.status)
        } catch (e: NetworkException) {
            throw e
        } catch (e: CancellationException) {
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
