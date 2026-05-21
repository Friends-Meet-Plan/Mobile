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
                in CLIENT_ERROR_RANGE -> {
                    "Ошибка запроса"
                }

                in SERVER_ERROR_RANGE -> {
                    "Ошибка сервера"
                }

                else -> {
                    "HTTP error"
                }
            }
        }

        ResultWrapper.Error(ApiError(code, message))
    } catch (e: IOException) {
        ResultWrapper.Error(
            ApiError(
                code = NO_CONNECTION_ERROR_CODE,
                message = "Нет подключения к интернету: ${e.message}",
            ),
        )
    } catch (e: TimeoutCancellationException) {
        ResultWrapper.Error(
            ApiError(
                code = UNKNOWN_ERROR_CODE,
                message = e.message ?: "Превышено время ожидания",
            ),
        )
    }
}
