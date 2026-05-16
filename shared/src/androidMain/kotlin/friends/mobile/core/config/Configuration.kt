package friends.mobile.core.config

import io.ktor.http.URLProtocol

data class AndroidConfiguration(
    override val isDebug: Boolean,
    override val baseHost: String = "10.0.2.2",
    override val basePort: Int = 3000,
    override val baseProtocol: URLProtocol = URLProtocol.HTTP,
    override val settingsName: String = "friends_settings",
    override val databaseName: String = "friends.db",
) : Configuration

actual fun createConfiguration(isDebug: Boolean): Configuration {
    return AndroidConfiguration(isDebug = isDebug)
}
