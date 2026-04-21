package friends.mobile.network

/** Typed failures surfaced by all network operations. */
sealed class NetworkException : Exception() {
    data object InvalidCredentials : NetworkException()
    data object Conflict : NetworkException()
    data object Unauthorized : NetworkException()
    data class NetworkError(val cause: Throwable) : NetworkException()
    data class UnknownError(val code: Int) : NetworkException()
}
