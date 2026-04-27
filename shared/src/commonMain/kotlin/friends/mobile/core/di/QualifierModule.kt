package friends.mobile.core.di

import friends.mobile.core.config.Configuration
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal object QualifierDBName
internal object QualifierSettingName

internal val qualifierModule = module {
    factory<String>(named<QualifierDBName>()) {
        get<Configuration>().databaseName
    }
    factory<String>(named<QualifierSettingName>()) {
        get<Configuration>().settingsName
    }
}
