package com.github.xingzheli.antibrainrot.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.xingzheli.antibrainrot.shared.AppGlobals

@Database(
    entities = [
        MonitorApps::class,
        UsageRecord::class,
        Metric::class,
        ControlRecord::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun monitorAppsAccessor() : MonitorAppsAccessor
    abstract fun usageRecordAccessor() : UsageRecordAccessor
    abstract fun metricAccessor() : MetricAccessor
    abstract fun controlRecordAccessor() : ControlRecordAccessor
}

fun buildAppDatabase(): AppDatabase {
    val application = AppGlobals.application
    return Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "appDatabase"
    ).build()
}