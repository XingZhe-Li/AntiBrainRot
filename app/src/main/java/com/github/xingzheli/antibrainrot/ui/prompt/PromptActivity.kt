package com.github.xingzheli.antibrainrot.ui.prompt

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUiLanguage
import com.github.xingzheli.antibrainrot.utils.LocaleHelper
import kotlinx.coroutines.runBlocking

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

    override fun attachBaseContext(base: Context) {
        val locale: String
        runBlocking {
            locale = getUiLanguage()
        }
        super.attachBaseContext(LocaleHelper.setLocale(base, locale))
    }
}