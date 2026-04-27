package friends.mobile.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module

internal typealias PlatformHttpEngineFactory = HttpClientEngineFactory<HttpClientEngineConfig>

internal expect val httpEngineFactoryModule: Module
