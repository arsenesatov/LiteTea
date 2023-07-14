package com.j7arsen.litetea.withsavedstate.ext

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

fun <State> StateFlow<State>.onEach(block: (State) -> Unit): StateFlow<State> =
    object : StateFlow<State> {
        override val replayCache: List<State>
            get() = this@onEach.replayCache

        override val value: State
            get() = this@onEach.value

        override suspend fun collect(collector: FlowCollector<State>): Nothing {
            this@onEach.collect {
                block(it)
                collector.emit(it)
            }
        }
    }
