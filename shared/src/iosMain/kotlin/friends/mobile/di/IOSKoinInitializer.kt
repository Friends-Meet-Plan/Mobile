package friends.mobile.di

import friends.mobile.core.CommonKmp
import friends.mobile.core.config.createConfiguration

fun initKoinIOS() = CommonKmp.initKoin(
    configuration = createConfiguration(isDebug = true),
)