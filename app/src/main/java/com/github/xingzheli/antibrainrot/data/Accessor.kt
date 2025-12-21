package com.github.xingzheli.antibrainrot.data

import com.github.xingzheli.antibrainrot.data.room.ControlRecord
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.github.xingzheli.antibrainrot.data.room.MonitorApps
import com.github.xingzheli.antibrainrot.data.room.UsageRecord
import com.github.xingzheli.antibrainrot.shared.AppGlobals
import kotlinx.coroutines.flow.Flow

// This file is for Accessor Singletons & Serve as a semi-repository (Service or DataProvider)
object Accessor {
    val monitorAppsAccessor by lazy { AppGlobals.appDatabase.monitorAppsAccessor() }
    val usageRecordAccessor by lazy { AppGlobals.appDatabase.usageRecordAccessor() }
    val metricAccessor by lazy { AppGlobals.appDatabase.metricAccessor() }
    val controlRecordAccessor by lazy { AppGlobals.appDatabase.controlRecordAccessor() }

    suspend fun insertMonitorApp(monitorApps: MonitorApps) {
        // This seems stupid , but maybe in the future you'd need some multi-database-operation process!
        monitorAppsAccessor.insert(monitorApps)
    }
    suspend fun removeMonitorAppWithPackageName(packageName: String) {
        monitorAppsAccessor.removeWithPackageName(packageName)
    }

    suspend fun getMonitorApps() : List<MonitorApps> {
        return monitorAppsAccessor.getMonitorApps()
    }

    fun getMonitorAppsFlow() : Flow<List<MonitorApps>> {
        return monitorAppsAccessor.getFlow()
    }

    suspend fun insertUsageRecord(usageRecord : UsageRecord) {
        usageRecordAccessor.insert(usageRecord)
    }

    suspend fun getUsageRecordWithRange(startTimeStamp : Long, endTimeStamp : Long) : List<UsageRecord> {
        return usageRecordAccessor.getUsageRecordWithRange(startTimeStamp, endTimeStamp)
    }

    suspend fun getLastUsageRecord() : UsageRecord? {
        return usageRecordAccessor.getLastRecord()
    }

    suspend fun getUsageRecordWithId(id: Long) : UsageRecord? {
        return usageRecordAccessor.getUsageRecordWithId(id)
    }

    suspend fun insertMetricRecord(metric : Metric) {
        metricAccessor.insert(metric)
    }

    suspend fun getMetricRecordWithRange(startTimeStamp : Long, endTimeStamp : Long) : List<Metric> {
        return metricAccessor.getMetricRecordWithRange(startTimeStamp, endTimeStamp)
    }

    suspend fun getLastMetricRecord() : Metric? {
        return metricAccessor.getLastMetric()
    }

    suspend fun insertControlRecord(record : ControlRecord) {
        controlRecordAccessor.insert(record)
    }

    suspend fun getControlRecordWithRange(startTimeStamp : Long, endTimeStamp : Long) : List<ControlRecord> {
        return controlRecordAccessor.getControlRecordWithRange(startTimeStamp, endTimeStamp)
    }
}