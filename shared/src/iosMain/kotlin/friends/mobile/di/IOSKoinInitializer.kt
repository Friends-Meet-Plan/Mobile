package friends.mobile.di

import friends.mobile.core.CommonKmp
import friends.mobile.core.config.Configuration

fun initKoinIOS() = CommonKmp.initKoin(
    configuration = Configuration(isDebug = true),
)
