package friends.mobile.core.domain.model

object ErrorMessages {
    const val NETWORK = "No internet connection. Please check your connection."
    const val UNAUTHORIZED = "Your session has expired. Please log in again."
    const val FORBIDDEN = "You don't have permission to perform this action."
    const val NOT_FOUND = "The requested resource was not found."
    const val CONFLICT = "This action cannot be completed. Try again."
    const val CLIENT_ERROR = "Please check your input and try again."
    const val SERVER_ERROR = "Server error. Please try again later."
    const val UNKNOWN = "Something went wrong. Please try again."
}

sealed class UserFriendlyError(open val message: String) {
    data class Network(override val message: String = ErrorMessages.NETWORK) : UserFriendlyError(message)
    data class Unauthorized(override val message: String = ErrorMessages.UNAUTHORIZED) : UserFriendlyError(message)
    data class Forbidden(override val message: String = ErrorMessages.FORBIDDEN) : UserFriendlyError(message)
    data class NotFound(override val message: String = ErrorMessages.NOT_FOUND) : UserFriendlyError(message)
    data class Conflict(override val message: String = ErrorMessages.CONFLICT) : UserFriendlyError(message)
    data class ClientError(override val message: String = ErrorMessages.CLIENT_ERROR) : UserFriendlyError(message)
    data class Server(override val message: String = ErrorMessages.SERVER_ERROR) : UserFriendlyError(message)
    data class Unknown(override val message: String = ErrorMessages.UNKNOWN) : UserFriendlyError(message)
}

fun mapApiErrorToUserFriendly(error: ApiError): UserFriendlyError = when (error.code) {
    401 -> UserFriendlyError.Unauthorized()
    403 -> UserFriendlyError.Forbidden()
    404 -> UserFriendlyError.NotFound()
    409 -> UserFriendlyError.Conflict()
    in 400..499 -> UserFriendlyError.ClientError()
    in 500..599 -> UserFriendlyError.Server()
    -1 -> UserFriendlyError.Network()
    else -> UserFriendlyError.Unknown()
}

fun getErrorMessage(error: UserFriendlyError): String = error.message
