package com.j7arsen.litetea.sample.withoutsavedstate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.j7arsen.litetea.withoutsavedstate.LiteTeaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SampleWithoutSavedStateViewModel :
    LiteTeaViewModel<SampleWithoutSavedStateMsg, SampleWithoutSavedStateState, SampleWithoutSavedStateEff>(
        initState = SampleWithoutSavedStateState(isLoading = true, message = ""),
        initEffects = setOf(SampleWithoutSavedStateEff.GenerateRandomMessage),
    ) {
    override fun reduce(
        msg: SampleWithoutSavedStateMsg,
        state: SampleWithoutSavedStateState,
    ): Pair<SampleWithoutSavedStateState, Set<SampleWithoutSavedStateEff>> {
        return when (msg) {
            SampleWithoutSavedStateMsg.GenerateAndShowRandomMessage -> state.copy(isLoading = true) to setOf(
                SampleWithoutSavedStateEff.GenerateRandomMessage,
            )

            is SampleWithoutSavedStateMsg.ShowRandomMessage -> state.copy(
                isLoading = false,
                message = msg.generatedMessage,
            ) to emptySet()
        }
    }

    override fun handleEffects(eff: SampleWithoutSavedStateEff, consumer: (msg: SampleWithoutSavedStateMsg) -> Unit) {
        when (eff) {
            is SampleWithoutSavedStateEff.GenerateRandomMessage -> effGenerateRandomMessageHandle(consumer)
        }
    }

    private fun effGenerateRandomMessageHandle(consumer: (msg: SampleWithoutSavedStateMsg) -> Unit) =
        viewModelScope.launch {
            consumer.invoke(SampleWithoutSavedStateMsg.ShowRandomMessage(generateRandomMessage()))
        }

    private suspend fun generateRandomMessage(): String {
        delay(3000)
        return "Random message - ${Random.nextLong(Long.MAX_VALUE)}"
    }
}

data class SampleWithoutSavedStateState(val isLoading: Boolean, val message: String)

sealed class SampleWithoutSavedStateMsg {
    object GenerateAndShowRandomMessage : SampleWithoutSavedStateMsg()
    data class ShowRandomMessage(val generatedMessage: String) : SampleWithoutSavedStateMsg()
}

sealed class SampleWithoutSavedStateEff {
    object GenerateRandomMessage : SampleWithoutSavedStateEff()
}

class SampleWithoutSavedStateViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SampleWithoutSavedStateViewModel() as T
    }
}
