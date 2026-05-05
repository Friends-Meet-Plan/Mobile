package friends.mobile.core.coroutines

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

@Suppress("unused")
fun Flow<*>.subscribe(
    onItem: (item: Any?) -> Unit,
    onComplete: () -> Unit,
    onThrow: (error: Throwable) -> Unit,
): Job = this
    .onEach { value -> onItem(value) }
    .catch { onThrow(it) }
    .onCompletion { onComplete() }
    .launchIn(MainScope())
