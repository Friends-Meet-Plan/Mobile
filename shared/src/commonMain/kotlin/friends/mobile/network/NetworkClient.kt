package friends.mobile.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_HOST = "localhost"
private const val BASE_PORT = 3000

/** Creates the shared base Ktor [HttpClient]. Pass to [AuthApi] and authenticated client. */
fun createHttpClient(json: Json): HttpClient = HttpClient(httpEngine()) {
    install(ContentNegotiation) { json(json) }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.BODY
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 5_000
        requestTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
    }
    defaultRequest {
        url {
            protocol = URLProtocol.HTTP
            host = BASE_HOST
            port = BASE_PORT
        }
    }
}
