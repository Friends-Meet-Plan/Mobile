package friends.mobile.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

fun <T> Flow<T>.subscribe(
    scope: CoroutineScope,
    onEach: (T) -> Unit
): Job = scope.launch {
    catch { }.collect { onEach(it) }
}