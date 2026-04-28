package friends.mobile.core.config

import io.ktor.http.URLProtocol

data class Configuration(
    val isDebug: Boolean,
    val baseHost: String = "10.0.2.2",
    val basePort: Int = 3000,
    val baseProtocol: URLProtocol = URLProtocol.HTTP,
    val settingsName: String = "friends_settings",
    val databaseName: String = "friends.db",
)
