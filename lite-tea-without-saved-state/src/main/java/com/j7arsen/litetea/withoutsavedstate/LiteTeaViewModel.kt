package com.j7arsen.litetea.withoutsavedstate

import com.j7arsen.litetea.core.LiteTeaCoreViewModel
import com.j7arsen.litetea.runtime.LiteTeaRuntime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class LiteTeaViewModel<Msg : Any, State : Any, Eff : Any>(
    initState: State,
    initEffects: Set<Eff>,
) : LiteTeaCoreViewModel<Msg, State, Eff>() {

    override val state: StateFlow<State>

    private val _state: MutableStateFlow<State>

    private val liteTeaRuntime: LiteTeaRuntime<Msg, State, Eff>

    init {
        _state = MutableStateFlow(initState)
        state = _state.asStateFlow()
        liteTeaRuntime = LiteTeaRuntime(
            initState = _state.value,
            initEffects = initEffects,
            stateListener = { state ->
                _state.tryEmit(state)
            },
            effectListener = { eff ->
                handleEffects(eff = eff) {
                    accept(msg = it)
                }
            },
            reduce = ::reduce,
        )
    }

    override fun accept(msg: Msg) {
        liteTeaRuntime.dispatch(msg = msg)
    }
}
