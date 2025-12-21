package com.github.xingzheli.antibrainrot.ui.prompt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class PromptActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = intent.getStringExtra("appName")
        val appPackageName = intent.getStringExtra("appPackageName")

        enableEdgeToEdge()
        setContent {
            PromptContainer(appName!!,appPackageName!!)
        }
    }
}