package com.j7arsen.litetea.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.j7arsen.litetea.sample.withoutsavedstate.SampleWithoutSavedStateActivity
import com.j7arsen.litetea.sample.withsavedstate.SampleWithSavedStateActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btnShowSampleWithoutSavedState).setOnClickListener {
            startActivity(Intent(this, SampleWithoutSavedStateActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnShowSampleWithSavedState).setOnClickListener {
            startActivity(Intent(this, SampleWithSavedStateActivity::class.java))
        }
    }
}
