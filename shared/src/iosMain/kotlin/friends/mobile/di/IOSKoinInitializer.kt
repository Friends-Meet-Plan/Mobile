package friends.mobile.di

import friends.mobile.core.CommonKmp
import friends.mobile.core.analytics.AnalyticsService
import friends.mobile.core.config.createConfiguration
import org.koin.dsl.module

fun initKoinIOS(analyticsService: AnalyticsService) = CommonKmp.initKoin(
    configuration = createConfiguration(isDebug = true),
    appDeclaration = {
        modules(module {
            single<AnalyticsService> { analyticsService }
        })
    },
)