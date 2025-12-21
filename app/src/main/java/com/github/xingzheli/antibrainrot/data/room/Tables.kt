package com.github.xingzheli.antibrainrot.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MonitorApps")
data class MonitorApps(
    @PrimaryKey
    val appPackageName      : String,
    val appName             : String,
)

@Entity(tableName = "UsageRecord")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true)
    val id                    : Long = 0,
    val startTimeStamp        : Long,
    val endTimeStamp          : Long,
    val appName               : String,
    val appPackageName        : String,
    val recordType            : String    // {"normal","untracked","inactivated"}
)

@Entity(tableName = "Metric")
data class Metric(
    @PrimaryKey(autoGenerate = true)
    val id                    : Long = 0,
    val timeStamp             : Long,
    val totalUsedTime         : Long,
    val useTimeFactor         : Double,
    val timeInDebt            : Double, // Or Bucket Size
    val maxTimeFactor         : Double
)

@Entity(tableName = "ControlRecord")
data class ControlRecord(
    @PrimaryKey(autoGenerate = true)
    val id                    : Long = 0,
    val timeStamp             : Long,
    val description           : String,
    val appPackageName        : String,
    val appName               : String
)