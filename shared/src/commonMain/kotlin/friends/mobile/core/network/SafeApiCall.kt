package friends.mobile.core.network

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException

/**
 * Executes a network call safely, catching common exceptions and wrapping the result.
 */
private const val NO_CONNECTION_ERROR_CODE = -1
private const val UNKNOWN_ERROR_CODE = -2

private val CLIENT_ERROR_RANGE = 400..499
private val SERVER_ERROR_RANGE = 500..599

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall())
    } catch (e: ResponseException) {
        val code = e.response.status.value

        val message = try {
            e.response.body<String>()
        } catch (_: Exception) {
            when (code) {
                400 -> "Please check your input and try again."
                401 -> "Your session has expired. Please log in again."
                403 -> "You don't have permission to perform this action."
                404 -> "The requested resource was not found."
                409 -> "This action cannot be completed. Try again."
                in CLIENT_ERROR_RANGE -> "Request error. Please try again."
                in SERVER_ERROR_RANGE -> "Server error. Please try again later."
                else -> "An error occurred. Please try again."
            }
        }

        ResultWrapper.Error(ApiError(code, message))
    } catch (e: IOException) {
        ResultWrapper.Error(
            ApiError(
                code = NO_CONNECTION_ERROR_CODE,
                message = "No internet connection. Please check your connection.",
            ),
        )
    } catch (e: TimeoutCancellationException) {
        ResultWrapper.Error(
            ApiError(
                code = UNKNOWN_ERROR_CODE,
                message = "Request timed out. Please try again.",
            ),
        )
    }
}
