package friends.mobile.auth.exception

/** Typed failures surfaced by all auth operations. */
sealed class AuthException : Exception() {
    data object InvalidCredentials : AuthException()
    data object Conflict : AuthException()
    data object Unauthorized : AuthException()
    data class NetworkError(val cause: Throwable) : AuthException()
    data class UnknownError(val code: Int) : AuthException()
}
