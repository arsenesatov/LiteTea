package com.j7arsen.litetea.sample.withsavedstate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.j7arsen.litetea.withsavedstate.LiteTeaSavedStateViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SampleWithSavedStateViewModel(savedStateHandle: SavedStateHandle) :
    LiteTeaSavedStateViewModel<SampleWithSavedStateMsg, SampleWithSavedStateState, SampleWithSavedStateEff>(
        initState = SampleWithSavedStateState(isLoading = true, message = ""),
        initEffects = setOf(SampleWithSavedStateEff.GenerateRandomMessage),
        savedStateKey = "SampleWithSavedStateViewModelSavedStateKey",
        savedStateHandle = savedStateHandle,
    ) {

    override fun reduce(
        msg: SampleWithSavedStateMsg,
        state: SampleWithSavedStateState,
    ): Pair<SampleWithSavedStateState, Set<SampleWithSavedStateEff>> {
        return when (msg) {
            SampleWithSavedStateMsg.GenerateAndShowRandomMessage -> state.copy(isLoading = true) to setOf(
                SampleWithSavedStateEff.GenerateRandomMessage,
            )

            is SampleWithSavedStateMsg.ShowRandomMessage -> state.copy(
                isLoading = false,
                message = msg.generatedMessage,
            ) to emptySet()
        }
    }

    override fun handleEffects(eff: SampleWithSavedStateEff, consumer: (msg: SampleWithSavedStateMsg) -> Unit) {
        when (eff) {
            is SampleWithSavedStateEff.GenerateRandomMessage -> effGenerateRandomMessageHandle(consumer)
        }
    }

    override fun restoreState(savedStateKey: String, savedStateHandle: SavedStateHandle): SampleWithSavedStateState? {
        val state = savedStateHandle.get<String>(savedStateKey)
        return if (state.isNullOrEmpty()) {
            null
        } else {
            kotlin.runCatching {
                Json.decodeFromString<SampleWithSavedStateState>(state)
            }.getOrNull()
        }
    }

    // override fun restoreState(savedStateKey: String, savedStateHandle: SavedStateHandle): SampleWithSavedStateState? {
    //     return savedStateHandle[savedStateKey]
    // }

    override fun saveState(
        savedStateKey: String,
        savedStateHandle: SavedStateHandle,
        state: SampleWithSavedStateState,
    ) {
        kotlin.runCatching { savedStateHandle[savedStateKey] = Json.encodeToString(state) }
    }

    // override fun saveState(
    //     savedStateKey: String,
    //     state: SampleWithSavedStateState,
    //     savedStateHandle: SavedStateHandle,
    // ) {
    //     kotlin.runCatching { savedStateHandle[savedStateKey] = state }
    // }

    override fun restore(restoreState: SampleWithSavedStateState): Pair<SampleWithSavedStateState, Set<SampleWithSavedStateEff>> {
        return if (restoreState.isLoading) {
            if (restoreState.message.isNotEmpty()) {
                restoreState.copy(isLoading = false) to emptySet()
            } else {
                restoreState to setOf(SampleWithSavedStateEff.GenerateRandomMessage)
            }
        } else {
            restoreState to emptySet()
        }
    }

    // override fun restore(restoreState: SampleWithSavedStateState): Pair<SampleWithSavedStateState, Set<SampleWithSavedStateEff>> {
    //     return restoreState to emptySet()
    // }

    private fun effGenerateRandomMessageHandle(consumer: (msg: SampleWithSavedStateMsg) -> Unit) =
        viewModelScope.launch {
            consumer.invoke(SampleWithSavedStateMsg.ShowRandomMessage(generateRandomMessage()))
        }

    private suspend fun generateRandomMessage(): String {
        delay(10000)
        return "Random message - ${Random.nextLong(Long.MAX_VALUE)}"
    }
}

@Serializable
data class SampleWithSavedStateState(val isLoading: Boolean, val message: String)

// @Parcelize
// data class SampleWithSavedStateState(val isLoading: Boolean, val message: String): Parcelable

sealed class SampleWithSavedStateMsg {
    object GenerateAndShowRandomMessage : SampleWithSavedStateMsg()
    data class ShowRandomMessage(val generatedMessage: String) : SampleWithSavedStateMsg()
}

sealed class SampleWithSavedStateEff {
    object GenerateRandomMessage : SampleWithSavedStateEff()
}

class SampleWithSavedStateViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return SampleWithSavedStateViewModel(
            savedStateHandle,
        ) as T
    }
}
