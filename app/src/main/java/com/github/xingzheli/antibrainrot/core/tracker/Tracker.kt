package com.github.xingzheli.antibrainrot.core.tracker

import android.view.accessibility.AccessibilityEvent
import com.github.xingzheli.antibrainrot.data.Accessor.getLastMetricRecord
import com.github.xingzheli.antibrainrot.data.Accessor.getLastUsageRecord
import com.github.xingzheli.antibrainrot.data.Accessor.getMonitorApps
import com.github.xingzheli.antibrainrot.data.Accessor.insertMetricRecord
import com.github.xingzheli.antibrainrot.data.Accessor.insertUsageRecord
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getConfigTrackMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlThreshold
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getTrackOn
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.github.xingzheli.antibrainrot.data.room.MonitorApps
import com.github.xingzheli.antibrainrot.data.room.UsageRecord
import com.github.xingzheli.antibrainrot.shared.AppGlobals
import com.github.xingzheli.antibrainrot.utils.FloatWindowProperty
import kotlinx.coroutines.launch

// This file / package is created for logics for tracking records

fun onEvent(event: AccessibilityEvent,floatWindowProperty: FloatWindowProperty) {
    if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        onWindowChangeEvent(event.packageName.toString(),floatWindowProperty)
    }
}

fun onWindowChangeEvent(appPackageName : String,floatWindowProperty: FloatWindowProperty) {
    // this function is meant to create record when receiving window change event
    val appCoroutineScope = AppGlobals.applicationCoroutineScope
    appCoroutineScope.launch {
        onWindowChangeEventSuspend(appPackageName,floatWindowProperty)
    }
}

suspend fun appendUsageRecord(
    appPackageName: String,
    timeStamp : Long,
    trackOn : Boolean,
    appList : List<MonitorApps>,
    lastUsageRecord: UsageRecord?
) : UsageRecord? {
    if (lastUsageRecord == null) {
        if (trackOn) {
            val appMatch = appList.find {
                it.appPackageName == appPackageName
            }
            if (appMatch == null) {
                val newLast = UsageRecord(
                    startTimeStamp = timeStamp,
                    endTimeStamp   = timeStamp,
                    appName        = "",
                    appPackageName = "",
                    recordType     = "untracked"
                )
                insertUsageRecord(newLast)
                return newLast
            } else {
                val newLast = UsageRecord(
                    startTimeStamp = timeStamp,
                    endTimeStamp   = timeStamp,
                    appName        = appMatch.appName,
                    appPackageName = appPackageName,
                    recordType     = "normal"
                )
                insertUsageRecord(newLast)
                return newLast
            }
        } else {
            val newLast = UsageRecord(
                startTimeStamp = timeStamp,
                endTimeStamp   = timeStamp,
                appName        = "",
                appPackageName = "",
                recordType     = "inactivated",
            )
            insertUsageRecord(newLast)
            return newLast
        }
    } else {
        if (trackOn) {
            val appMatch = appList.find {
                it.appPackageName == appPackageName
            }

            if (appMatch == null) {
                var newLast = lastUsageRecord.copy(
                    endTimeStamp = timeStamp
                )
                insertUsageRecord(newLast)
                if (lastUsageRecord.recordType != "untracked") {
                    newLast = UsageRecord(
                        startTimeStamp = timeStamp,
                        endTimeStamp   = timeStamp,
                        appName        = "",
                        appPackageName = "",
                        recordType     = "untracked"
                    )
                    insertUsageRecord(newLast)
                }
                return newLast
            } else {
                var newLast = lastUsageRecord.copy(
                    endTimeStamp = timeStamp
                )
                insertUsageRecord(newLast)
                if (lastUsageRecord.recordType != "normal" || lastUsageRecord.appPackageName != appPackageName) {
                    newLast = UsageRecord(
                        startTimeStamp = timeStamp,
                        endTimeStamp   = timeStamp,
                        appName = appMatch.appName,
                        appPackageName = appPackageName,
                        recordType     = "normal"
                    )
                    insertUsageRecord(newLast)
                }
                return newLast
            }
        } else {
            // update last record
            var newLast = lastUsageRecord.copy(
                endTimeStamp = timeStamp
            )
            insertUsageRecord(newLast)
            if (lastUsageRecord.recordType != "inactivated") {
                newLast = UsageRecord(
                    startTimeStamp = timeStamp,
                    endTimeStamp   = timeStamp,
                    appName        = "",
                    appPackageName = "",
                    recordType     = "inactivated"
                )
                insertUsageRecord(newLast)
            }
            return newLast
        }
    }
}

suspend fun appendMetricRecord(
    timeStamp : Long,
    lastUsageRecord: UsageRecord?,
    lastMetricRecord: Metric?
) : Pair<Double,Double>? {
    if (lastUsageRecord == null || lastUsageRecord.recordType == "inactivated") {
        return null// No Metrics Update is needed for this two situations
    }
    val metricBase = lastMetricRecord
        ?: Metric(
            timeStamp  = 0,
            totalUsedTime = 0,
            useTimeFactor = 0.0,
            timeInDebt = 0.0,
            maxTimeFactor = 0.0
        )
    val usingInt = if (lastUsageRecord.recordType == "normal") 1L else 0L
    val usedTimeIncrement = timeStamp - lastUsageRecord.endTimeStamp

    val newUseTimeFactor = updateUseTimeFactor(
        usingInt,
        metricBase.useTimeFactor,
        metricBase.totalUsedTime,
        usedTimeIncrement
    )

    val newTimeInDebt = updateTimeInDebt(
        usingInt,
        metricBase.timeInDebt,
        usedTimeIncrement,
        metricBase.useTimeFactor,
    )

    val newMaxTimeFactor = updateMaxTimeFactor(
        newTimeInDebt,
        metricBase.maxTimeFactor,
        metricBase.totalUsedTime,
        usedTimeIncrement
    )

    insertMetricRecord(
        Metric(
            timeStamp = timeStamp,
            totalUsedTime = metricBase.totalUsedTime + usedTimeIncrement,
            useTimeFactor = newUseTimeFactor,
            timeInDebt    = newTimeInDebt,
            maxTimeFactor = newMaxTimeFactor
        )
    )

    return newUseTimeFactor to newMaxTimeFactor
}

suspend fun appendMetricRecordWithLast(
    timeStamp : Long,
    lastUsageRecord: UsageRecord?,
    lastMetricRecord: Metric?
) : Metric? {
    if (lastUsageRecord == null || lastUsageRecord.recordType == "inactivated") {
        return null// No Metrics Update is needed for this two situations
    }
    val metricBase = lastMetricRecord
        ?: Metric(
            timeStamp  = 0,
            totalUsedTime = 0,
            useTimeFactor = 0.0,
            timeInDebt = 0.0,
            maxTimeFactor = 0.0
        )
    val usingInt = if (lastUsageRecord.recordType == "normal") 1L else 0L
    val usedTimeIncrement = timeStamp - lastUsageRecord.endTimeStamp

    val newUseTimeFactor = updateUseTimeFactor(
        usingInt,
        metricBase.useTimeFactor,
        metricBase.totalUsedTime,
        usedTimeIncrement
    )

    val newTimeInDebt = updateTimeInDebt(
        usingInt,
        metricBase.timeInDebt,
        usedTimeIncrement,
        metricBase.useTimeFactor,
    )

    val newMaxTimeFactor = updateMaxTimeFactor(
        newTimeInDebt,
        metricBase.maxTimeFactor,
        metricBase.totalUsedTime,
        usedTimeIncrement
    )

    val newLast = Metric(
        timeStamp = timeStamp,
        totalUsedTime = metricBase.totalUsedTime + usedTimeIncrement,
        useTimeFactor = newUseTimeFactor,
        timeInDebt    = newTimeInDebt,
        maxTimeFactor = newMaxTimeFactor
    )

    insertMetricRecord(newLast)
    return newLast
}


suspend fun onWindowChangeEventSuspend(appPackageName: String,floatWindowProperty: FloatWindowProperty) {
    // if track Method is set to other options, accessibility based tracking would be disabled
    val trackMethod = getConfigTrackMethod()
    if (trackMethod != "accessibility") return

    val timeStamp = System.currentTimeMillis()
    val trackOn = getTrackOn()
    val appList = getMonitorApps()
    val lastUsageRecord = getLastUsageRecord()
    val lastMetricRecord = getLastMetricRecord()
    appendUsageRecord(appPackageName,timeStamp,trackOn,appList,lastUsageRecord)
    val metricPair = appendMetricRecord(timeStamp,lastUsageRecord,lastMetricRecord)

    checkIntervene(
        metricPair,appList,lastUsageRecord,appPackageName,floatWindowProperty,timeStamp
    )
}

fun onPause() {
    val appCoroutineScope = AppGlobals.applicationCoroutineScope
    appCoroutineScope.launch {
        onPauseSuspend()
    }
}

suspend fun appendUsageRecordWhenPausing(
    timeStamp : Long,
    lastUsageRecord: UsageRecord?
) {
    if (lastUsageRecord == null) {
        insertUsageRecord(
            UsageRecord(
                startTimeStamp = timeStamp,
                endTimeStamp   = timeStamp,
                appName        = "",
                appPackageName = "",
                recordType     = "inactivated"
            )
        )
    } else {
        insertUsageRecord(
            lastUsageRecord.copy(
                endTimeStamp = timeStamp
            )
        )
        if (lastUsageRecord.recordType != "inactivated") {
            insertUsageRecord(
                UsageRecord(
                    startTimeStamp = timeStamp,
                    endTimeStamp   = timeStamp,
                    appName        = "",
                    appPackageName = "",
                    recordType     = "inactivated"
                )
            )
        }
    }
}
suspend fun onPauseSuspend() {
    // if track Method is set to other options, accessibility based tracking would be disabled
    val trackMethod = getConfigTrackMethod()
    if (trackMethod != "accessibility") return

    val timeStamp = System.currentTimeMillis()
    val lastUsageRecord = getLastUsageRecord()
    val lastMetricRecord = getLastMetricRecord()

    appendUsageRecordWhenPausing(timeStamp,lastUsageRecord)
    appendMetricRecord(timeStamp,lastUsageRecord,lastMetricRecord)
}