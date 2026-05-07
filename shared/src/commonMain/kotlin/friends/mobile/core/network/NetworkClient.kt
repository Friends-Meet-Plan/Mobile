package friends.mobile.core.network

import friends.mobile.core.config.Configuration
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    includes(httpEngineFactoryModule)

    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<HttpClient> {
        buildHttpClient(
            engine = get(),
            json = get(),
            configuration = get(),
        )
    }
}

private fun buildHttpClient(
    engine: HttpClientEngineFactory<HttpClientEngineConfig>,
    json: Json,
    configuration: Configuration,
): HttpClient = HttpClient(engine) {
    // JSON
    install(ContentNegotiation) {
        json(json)
    }

    // Logging
    install(Logging) {
        logger = Logger.SIMPLE
        level = if (configuration.isDebug) LogLevel.BODY else LogLevel.NONE
    }

    // Timeouts
    install(HttpTimeout) {
        connectTimeoutMillis = 5_000
        requestTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
    }

    // Retry
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        exponentialDelay()
    }

    // Base config for ALL requests
    defaultRequest {
        url {
            protocol = configuration.baseProtocol
            host = configuration.baseHost
            port = configuration.basePort
        }

        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }

    // Global error handling
    HttpResponseValidator {
        validateResponse { response: HttpResponse ->
            if (!response.status.isSuccess()) {
                throw mapStatus(response)
            }
        }

        handleResponseExceptionWithRequest { cause: Throwable, _ ->
            throw when (cause) {
                is NetworkException -> cause
                is CancellationException -> cause
                else -> NetworkException.NetworkError(cause)
            }
        }
    }
}

private fun mapStatus(response: HttpResponse): NetworkException =
    when (response.status) {
        HttpStatusCode.Unauthorized -> NetworkException.InvalidCredentials
        HttpStatusCode.Conflict -> NetworkException.Conflict
        else -> NetworkException.UnknownError(response.status.value)
    }
