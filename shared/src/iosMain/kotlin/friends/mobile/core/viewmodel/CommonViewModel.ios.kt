package friends.mobile.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

actual abstract class CommonViewModel actual constructor() {
    actual val viewModelScope: CoroutineScope = MainScope()

    protected actual open fun onCleared() {}

    actual fun clear() {
        onCleared()
        viewModelScope.cancel()
    }
}
