package com.j7arsen.litetea.runtime

class LiteTeaRuntime<Msg : Any, State : Any, Eff : Any>(
    initState: State,
    initEffects: Set<Eff>,
    private val stateListener: (state: State) -> Unit,
    private val effectListener: (eff: Eff) -> Unit,
    private val reduce: (msg: Msg, state: State) -> Pair<State, Set<Eff>>,
) {

    private var currentState: State = initState

    init {
        currentState = initState
        executeInitEffects(initEffects = initEffects)
    }

    fun dispatch(msg: Msg) {
        val (newState, effects) = reduce(msg, currentState)
        if (currentState != newState) {
            currentState = newState
            stateListener.invoke(newState)
        }
        executeEffects(effects = effects)
    }

    private fun executeInitEffects(initEffects: Set<Eff>) {
        if (initEffects.isNotEmpty()) {
            executeEffects(effects = initEffects)
        }
    }

    private fun executeEffects(effects: Set<Eff>) {
        effects.forEach { eff ->
            effectListener.invoke(eff)
        }
    }
}
