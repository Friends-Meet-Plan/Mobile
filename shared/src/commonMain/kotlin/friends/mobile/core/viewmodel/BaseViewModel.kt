package friends.mobile.core.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State : Any, Action, Event>(
    initState: State,
) : ViewModel() {

    protected val viewModelScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main,
    )

    private val mutableState = MutableStateFlow(initState)
    private val mutableAction = MutableSharedFlow<Action>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    protected var viewState: State
        get() = mutableState.value
        set(value) {
            mutableState.value = value
        }

    protected var viewAction: Action
        get() = error("Get Action disable")
        set(value) {
            mutableAction.tryEmit(value)
        }

    val viewStates: StateFlow<State>
        get() = mutableState.asStateFlow()

    val viewActions: SharedFlow<Action>
        get() = mutableAction.asSharedFlow()

    abstract fun obtainEvent(event: Event)

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
