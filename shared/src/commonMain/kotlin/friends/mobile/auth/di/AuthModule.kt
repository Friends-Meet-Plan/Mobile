package friends.mobile.auth.di

import friends.mobile.auth.remote.AuthApi
import friends.mobile.auth.remote.AuthenticatedClient
import friends.mobile.auth.repository.AuthRepository
import friends.mobile.auth.repository.AuthRepositoryImpl
import friends.mobile.auth.storage.TokenStorage
import friends.mobile.auth.storage.TokenStorageImpl
import friends.mobile.network.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the auth feature.
 *
 * Prerequisites in your Koin graph before including this module:
 *   - single<com.russhwolf.settings.Settings> { ... }  (platform-specific, see below)
 *
 * Android:
 *   single<Settings> { SharedPreferencesSettings(androidContext().getSharedPreferences("friends_auth", Context.MODE_PRIVATE)) }
 * iOS (in iosMain platform module):
 *   single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
 */
val authModule = module {

    single<Json> {
        Json { ignoreUnknownKeys = true; isLenient = true }
    }

    // Base unauthenticated client — used by AuthApi (login, register, refresh, logout)
    single<HttpClient> {
        createHttpClient(json = get())
    }

    // Authenticated client — HttpSend interceptor injects Bearer + handles 401 refresh
    single<HttpClient>(named("auth")) {
        val authClient = createHttpClient(json = get())
        AuthenticatedClient(
            client = authClient,
            storage = get(),
            onRefresh = { get<AuthRepository>().refresh() },
            onUnauthorized = { get<AuthRepository>().logout() },
        )
        authClient
    }

    single<TokenStorage> {
        TokenStorageImpl(settings = get(), json = get())
    }

    single<AuthApi> {
        AuthApi(client = get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(api = get(), storage = get())
    }
}
