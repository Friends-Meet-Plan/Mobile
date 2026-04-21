package friends.mobile.network

import io.ktor.client.engine.HttpClientEngineFactory

/** Platform-specific Ktor engine (OkHttp on Android, Darwin on iOS). */
internal expect fun httpEngine(): HttpClientEngineFactory<*>
