package friends.mobile.core

import friends.mobile.core.config.Configuration
import friends.mobile.core.di.platformModule
import friends.mobile.core.di.qualifierModule
import friends.mobile.core.network.networkModule
import friends.mobile.feature.featureModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

object CommonKmp {

    fun initKoin(
        configuration: Configuration,
        appDeclaration: KoinAppDeclaration = {},
    ) {
        startKoin {
            appDeclaration()
            modules(
                createConfiguration(configuration),
                qualifierModule,
                platformModule,
                networkModule,
                featureModule,
            )
        }
    }

    private fun createConfiguration(configuration: Configuration) = module {
        single<Configuration> { configuration }
    }
}
