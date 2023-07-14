package com.j7arsen.litetea.sample.withsavedstate

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.j7arsen.litetea.sample.R
import kotlinx.coroutines.launch

class SampleWithSavedStateActivity : AppCompatActivity() {

    private val viewModel: SampleWithSavedStateViewModel by viewModels { SampleWithSavedStateViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val pbLoading: ProgressBar = findViewById(R.id.pbLoading)
        val tvMessage: TextView = findViewById(R.id.tvMessage)

        val btnShowRandomMessage = findViewById<MaterialButton>(R.id.btnShowRandomMessage)
        btnShowRandomMessage.setOnClickListener {
            viewModel.accept(SampleWithSavedStateMsg.GenerateAndShowRandomMessage)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (pbLoading.isVisible != state.isLoading) {
                        pbLoading.isVisible = state.isLoading
                    }
                    if (tvMessage.isVisible == state.isLoading) {
                        tvMessage.isVisible = !state.isLoading
                    }
                    if (btnShowRandomMessage.isEnabled == state.isLoading) {
                        btnShowRandomMessage.isEnabled = !state.isLoading
                    }
                    if (tvMessage.text.toString() != state.message) {
                        tvMessage.text = state.message
                    }
                }
            }
        }
    }
}
