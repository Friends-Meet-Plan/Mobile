package friends.mobile.core.network

import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

internal actual val httpEngineFactoryModule = module {
    single<PlatformHttpEngineFactory> {
        OkHttp
    }
}
