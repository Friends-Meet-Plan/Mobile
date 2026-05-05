package friends.mobile.core.network

import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

internal actual val httpEngineFactoryModule = module {
    single<PlatformHttpEngineFactory> {
        Darwin
    }
}
