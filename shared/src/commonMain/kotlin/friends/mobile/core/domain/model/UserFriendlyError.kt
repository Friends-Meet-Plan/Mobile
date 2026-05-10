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

sealed class UserFriendlyError {
    data object Network : UserFriendlyError()
    data object Unauthorized : UserFriendlyError()
    data object Forbidden : UserFriendlyError()
    data object NotFound : UserFriendlyError()
    data object Conflict : UserFriendlyError()
    data object ClientError : UserFriendlyError()
    data object Server : UserFriendlyError()
    data object Unknown : UserFriendlyError()
}

fun mapApiErrorToUserFriendly(error: ApiError): UserFriendlyError = when (error.code) {
    401 -> UserFriendlyError.Unauthorized
    403 -> UserFriendlyError.Forbidden
    404 -> UserFriendlyError.NotFound
    409 -> UserFriendlyError.Conflict
    in 400..499 -> UserFriendlyError.ClientError
    in 500..599 -> UserFriendlyError.Server
    -1 -> UserFriendlyError.Network
    else -> UserFriendlyError.Unknown
}

fun getErrorMessage(error: UserFriendlyError): String = when (error) {
    UserFriendlyError.Network -> ErrorMessages.NETWORK
    UserFriendlyError.Unauthorized -> ErrorMessages.UNAUTHORIZED
    UserFriendlyError.Forbidden -> ErrorMessages.FORBIDDEN
    UserFriendlyError.NotFound -> ErrorMessages.NOT_FOUND
    UserFriendlyError.Conflict -> ErrorMessages.CONFLICT
    UserFriendlyError.ClientError -> ErrorMessages.CLIENT_ERROR
    UserFriendlyError.Server -> ErrorMessages.SERVER_ERROR
    UserFriendlyError.Unknown -> ErrorMessages.UNKNOWN
}
