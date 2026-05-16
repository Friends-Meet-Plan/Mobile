package friends.mobile.core.config

import io.ktor.http.URLProtocol

interface Configuration {
    val isDebug: Boolean
    val baseHost: String
    val basePort: Int
    val baseProtocol: URLProtocol
    val settingsName: String
    val databaseName: String
}

expect fun createConfiguration(isDebug: Boolean): Configuration
