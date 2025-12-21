package com.github.xingzheli.antibrainrot.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitorAppsAccessor {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(monitorApps: MonitorApps)

    @Query("SELECT * FROM MonitorApps")
    fun getFlow(): Flow<List<MonitorApps>>

    @Query("SELECT * FROM MonitorApps")
    suspend fun getMonitorApps() : List<MonitorApps>

    @Query("DELETE FROM MonitorApps WHERE :appPackageName = appPackageName")
    suspend fun removeWithPackageName(appPackageName: String)

    @Delete
    suspend fun remove(monitorApps: MonitorApps)

    @Query("DELETE FROM MonitorApps")
    suspend fun deleteAll()
}

@Dao
interface UsageRecordAccessor {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usageRecord : UsageRecord)

    @Query("SELECT * FROM UsageRecord WHERE :startTimeStamp <= startTimeStamp AND :endTimeStamp >= endTimeStamp")
    suspend fun getUsageRecordWithRange(startTimeStamp : Long, endTimeStamp : Long) : List<UsageRecord>

    @Query("SELECT * FROM UsageRecord ORDER BY id DESC LIMIT 1 ")
    suspend fun getLastRecord(): UsageRecord?

    @Query("SELECT * FROM UsageRecord WHERE id = :id")
    suspend fun getUsageRecordWithId(id : Long) : UsageRecord?
}

@Dao
interface MetricAccessor {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric : Metric)

    @Query("SELECT * FROM Metric WHERE :startTimeStamp <= timeStamp AND :endTimeStamp >= timeStamp")
    suspend fun getMetricRecordWithRange(startTimeStamp: Long, endTimeStamp: Long) : List<Metric>

    @Query("SELECT * FROM Metric ORDER BY id DESC LIMIT 1")
    suspend fun getLastMetric(): Metric?
}

@Dao
interface ControlRecordAccessor {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record : ControlRecord)

    @Query("SELECT * FROM ControlRecord WHERE :startTimeStamp <= timeStamp AND :endTimeStamp >= timeStamp")
    suspend fun getControlRecordWithRange(startTimeStamp: Long, endTimeStamp: Long) : List<ControlRecord>
}