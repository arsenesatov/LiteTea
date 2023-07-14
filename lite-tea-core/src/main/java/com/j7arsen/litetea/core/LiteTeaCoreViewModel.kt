package com.j7arsen.litetea.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class LiteTeaCoreViewModel<Msg : Any, State : Any, Eff : Any> : ViewModel() {

    abstract val state: StateFlow<State>

    abstract fun accept(msg: Msg)

    protected abstract fun reduce(msg: Msg, state: State): Pair<State, Set<Eff>>

    protected abstract fun handleEffects(eff: Eff, consumer: (msg: Msg) -> Unit)
}
