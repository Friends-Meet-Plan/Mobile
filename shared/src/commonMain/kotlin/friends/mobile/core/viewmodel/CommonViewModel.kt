package friends.mobile.core.viewmodel

import kotlinx.coroutines.CoroutineScope

expect abstract class CommonViewModel() {
    val viewModelScope: CoroutineScope
    protected open fun onCleared()

    /**
     * Explicit cleanup hook for iOS (called from Swift wrapper `deinit`).
     * On Android it is usually not needed because lifecycle calls [onCleared].
     */
    fun clear()
}
