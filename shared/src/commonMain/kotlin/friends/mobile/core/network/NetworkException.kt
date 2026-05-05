package friends.mobile.core.network

sealed class NetworkException : Exception() {
    data object InvalidCredentials : NetworkException()
    data object Conflict : NetworkException()
    data object Unauthorized : NetworkException()
    data class NetworkError(override val cause: Throwable) : NetworkException()
    data class UnknownError(val code: Int) : NetworkException()
}
