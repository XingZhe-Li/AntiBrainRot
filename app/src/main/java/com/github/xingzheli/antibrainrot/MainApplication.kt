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

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.xingzheli.antibrainrot.data.room.AppDatabase
import com.github.xingzheli.antibrainrot.data.room.buildAppDatabase
import com.github.xingzheli.antibrainrot.shared.AppGlobals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainApplication : Application() {

    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val appDatabase : AppDatabase by lazy {
        buildAppDatabase()
    }
    val preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencesDataStore")

    override fun onCreate() {
        super.onCreate()
        AppGlobals.application = this
    }
}