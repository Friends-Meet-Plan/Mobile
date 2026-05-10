package friends.mobile.core.domain.model

sealed class ResultWrapper<out T> {

    data class Success<out T>(
        val data: T,
    ) : ResultWrapper<T>()

    data class Error(
        val error: ApiError,
    ) : ResultWrapper<Nothing>()

    inline fun <R> map(
        transform: (T) -> R,
    ): ResultWrapper<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }

    /**
     * Returns data or throws domain-specific exception.
     */
    fun getOrThrow(): T {
        return when (this) {
            is Success -> data
            is Error -> throw ApiException(error)
        }
    }
}

data class ApiError(
    val code: Int,
    val message: String,
)

class ApiException(
    val apiError: ApiError,
) : IllegalStateException(apiError.message)
