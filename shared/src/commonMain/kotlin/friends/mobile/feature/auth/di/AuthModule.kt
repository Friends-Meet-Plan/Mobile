package friends.mobile.feature.auth.di

import friends.mobile.feature.auth.data.remote.AuthApi
import friends.mobile.feature.auth.data.remote.AuthenticatedClient
import friends.mobile.feature.auth.data.repository.AuthRepositoryImpl
import friends.mobile.feature.auth.data.storage.TokenStorage
import friends.mobile.feature.auth.data.storage.TokenStorageImpl
import friends.mobile.feature.auth.domain.repository.AuthRepository
import io.ktor.client.HttpClient
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
    single<HttpClient>(named("auth")) {
        val authClient = get<HttpClient>().config { }
        AuthenticatedClient(
            client = authClient,
            storage = get(),
            onRefresh = { get<AuthRepository>().refresh() },
            onUnauthorized = { get<AuthRepository>().logout() },
        )
        authClient
    }

    single<TokenStorage> {
        TokenStorageImpl(
            settings = get(),
            json = get(),
        )
    }

    single<AuthApi> {
        AuthApi(client = get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            api = get(),
            storage = get(),
        )
    }
}
