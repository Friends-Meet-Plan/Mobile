package friends.mobile.core.domain.model

object ErrorMessages {
    const val NETWORK = "Ошибка сети. Проверьте соединение"
    const val UNAUTHORIZED = "Необходимо войти в аккаунт"
    const val FORBIDDEN = "Доступ к данным запрещён"
    const val NOT_FOUND = "Данные не найдены"
    const val CONFLICT = "Конфликт данных: действие невозможно в текущем состоянии"
    const val CLIENT_ERROR = "Ошибка запроса. Попробуйте снова"
    const val SERVER_ERROR = "Сервер временно недоступен"
    const val UNKNOWN = "Произошла неизвестная ошибка"
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
    401 -> UserFriendlyError.Unauthorized(error.message)
    403 -> UserFriendlyError.Forbidden(error.message)
    404 -> UserFriendlyError.NotFound(error.message)
    409 -> UserFriendlyError.Conflict(error.message)
    in 400..499 -> UserFriendlyError.ClientError(error.message)
    in 500..599 -> UserFriendlyError.Server(error.message)
    -1 -> UserFriendlyError.Network(error.message)
    else -> UserFriendlyError.Unknown(error.message)
}

fun getErrorMessage(error: UserFriendlyError): String = error.message
