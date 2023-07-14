package com.j7arsen.litetea.withsavedstate

import androidx.lifecycle.SavedStateHandle
import com.j7arsen.litetea.core.LiteTeaCoreViewModel
import com.j7arsen.litetea.runtime.LiteTeaRuntime
import com.j7arsen.litetea.withsavedstate.ext.onEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class LiteTeaSavedStateViewModel<Msg : Any, State : Any, Eff : Any>(
    initState: State,
    initEffects: Set<Eff>,
    savedStateKey: String,
    savedStateHandle: SavedStateHandle,
) : LiteTeaCoreViewModel<Msg, State, Eff>() {

    override val state: StateFlow<State>

    private val _state: MutableStateFlow<State>

    private val liteTeaRuntime: LiteTeaRuntime<Msg, State, Eff>

    init {
        val (initialState: State, initialEffects: Set<Eff>) = restore(
            restoreState = restoreState(
                savedStateKey = savedStateKey,
                savedStateHandle = savedStateHandle,
            ),
            initState = initState,
            initEffects = initEffects,
        )
        _state = MutableStateFlow(initialState)
        state = _state.asStateFlow().onEach {
            saveState(savedStateKey = savedStateKey, savedStateHandle = savedStateHandle, state = it)
        }
        liteTeaRuntime = LiteTeaRuntime(
            initState = _state.value,
            initEffects = initialEffects,
            stateListener = { state -> _state.tryEmit(state) },
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

    protected abstract fun restoreState(savedStateKey: String, savedStateHandle: SavedStateHandle): State?

    protected abstract fun saveState(savedStateKey: String, savedStateHandle: SavedStateHandle, state: State)

    protected abstract fun restore(restoreState: State): Pair<State, Set<Eff>>

    private fun restore(restoreState: State?, initState: State, initEffects: Set<Eff>): Pair<State, Set<Eff>> {
        return if (restoreState == null) {
            initState to initEffects
        } else {
            restore(restoreState = restoreState)
        }
    }
}
