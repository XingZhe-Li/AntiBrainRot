package com.github.xingzheli.antibrainrot.core.tracker

import android.app.usage.UsageEvents
import com.github.xingzheli.antibrainrot.data.Accessor.getLastMetricRecord
import com.github.xingzheli.antibrainrot.data.Accessor.getLastUsageRecord
import com.github.xingzheli.antibrainrot.data.Accessor.getMonitorApps
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getConfigTrackMethod
import com.github.xingzheli.antibrainrot.shared.AppGlobals
import com.github.xingzheli.antibrainrot.utils.getUsageEvents

data class AppUsageInfo(
    val packageName: String,
    val timestamp: Long,
    val eventType: Int
)

suspend fun appUsageUpdater() {
    val currentTime = System.currentTimeMillis()
    val appList = getMonitorApps()
    var lastUsageRecord = getLastUsageRecord()
    var lastMetricRecord = getLastMetricRecord()
    val appContext = AppGlobals.application

    val usageEvents = getUsageEvents(appContext,lastUsageRecord?.endTimeStamp ?: 0L ,currentTime)
    for (usageEvent in usageEvents.sortedBy { it.timestamp }) {
        if (usageEvent.eventType != UsageEvents.Event.ACTIVITY_RESUMED && usageEvent.eventType != UsageEvents.Event.SCREEN_NON_INTERACTIVE) continue
        val appPackageName = if (usageEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) usageEvent.packageName else ""
        val newLastUsageRecord = appendUsageRecord(appPackageName,usageEvent.timestamp,true,appList,lastUsageRecord)
        val newLastMetricRecord = appendMetricRecordWithLast(usageEvent.timestamp,lastUsageRecord,lastMetricRecord)

        lastUsageRecord = if (lastUsageRecord != null && newLastUsageRecord.id == 0L) {
            newLastUsageRecord.copy(id = lastUsageRecord.id + 1)
        } else if (lastUsageRecord == null) {
            newLastUsageRecord.copy(id = 1L)
        } else {
            newLastUsageRecord
        }

        lastMetricRecord = newLastMetricRecord
    }
}

suspend fun onAppEffect() {
    val trackMethod = getConfigTrackMethod()
    if (trackMethod != "appUsageEvent") return
    appUsageUpdater()
}