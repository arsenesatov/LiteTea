package com.j7arsen.litetea.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.j7arsen.litetea.core.LiteTeaCoreViewModel

// https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda

@Composable
fun <Msg : Any, State : Any, Eff : Any> LiteTeaCoreViewModel<Msg, State, Eff>.collectState(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    stateFlow: (suspend (state: State) -> Unit),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(state, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            state.collect { stateFlow(it) }
        }
    }
}

@Composable
fun <Msg : Any, State : Any, Eff : Any> LiteTeaCoreViewModel<Msg, State, Eff>.collectAsState(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED
): androidx.compose.runtime.State<State> {
    val lifecycleOwner = LocalLifecycleOwner.current

    val stateFlowLifecycleAware = remember(state, lifecycleOwner) {
        state.flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
    }

    // Need to access the initial value to convert to State - collectAsState() suppresses this lint warning too
    @SuppressLint("StateFlowValueCalledInComposition")
    val initialValue = state.value
    return stateFlowLifecycleAware.collectAsState(initialValue)
}
