package com.github.xingzheli.antibrainrot.shared

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.xingzheli.antibrainrot.MainApplication
import com.github.xingzheli.antibrainrot.data.room.AppDatabase
import kotlinx.coroutines.CoroutineScope

object AppGlobals {
    lateinit var application : MainApplication
    val appDatabase : AppDatabase
        get() = application.appDatabase
    val preferencesDataStore : DataStore<Preferences>
        get() = application.preferenceDataStore

    val applicationCoroutineScope : CoroutineScope
        get() = application.coroutineScope
}