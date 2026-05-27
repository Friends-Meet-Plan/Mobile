package friends.mobile.core.viewmodel

import friends.mobile.core.analytics.AnalyticsService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<State : Any, Action, Event>(
    initState: State,
    screenName: String? = null,
) : CommonViewModel(), KoinComponent {

    private val analyticsService: AnalyticsService by inject()

    private val mutableState = MutableStateFlow(initState)
    private val mutableAction = MutableSharedFlow<Action>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    init {
        screenName?.let { analyticsService.logScreenView(it) }
    }

    protected fun logError(throwable: Throwable) {
        analyticsService.logError(throwable)
    }

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
}
