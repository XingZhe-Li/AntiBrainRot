/*
    AntiBrainRot for Tracking & Controlling AppUsage
    Copyright (C) 2025 XingZhe-Li

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/

package com.github.xingzheli.antibrainrot

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUiLanguage
import com.github.xingzheli.antibrainrot.ui.interfaces.app.AppContainer
import com.github.xingzheli.antibrainrot.utils.LocaleHelper
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppContainer() }
    }

    override fun attachBaseContext(base: Context) {
        val locale: String
        runBlocking {
            locale = getUiLanguage()
        }
        super.attachBaseContext(LocaleHelper.setLocale(base, locale))
    }
}