package com.github.xingzheli.antibrainrot.core.tracker

import android.content.Intent
import com.github.xingzheli.antibrainrot.data.Accessor.getLastUsageRecord
import com.github.xingzheli.antibrainrot.data.Accessor.getUsageRecordWithId
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlBypassExpireInterval
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlThreshold
import com.github.xingzheli.antibrainrot.data.room.MonitorApps
import com.github.xingzheli.antibrainrot.data.room.UsageRecord
import com.github.xingzheli.antibrainrot.ui.prompt.PromptActivity
import com.github.xingzheli.antibrainrot.utils.FloatWindowProperty
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// Haha , this Controller is really the "Controller" ,
// which controls the user.

val lastBypassedIdLock = Mutex()
var lastBypassedId : Long = -1L

suspend fun checkIfLastBypassValid(timeStamp : Long) : Boolean {
    val bypassExpireInterval = getControlBypassExpireInterval()
    if (lastBypassedId == -1L) return false
    lastBypassedIdLock.withLock {
        val lastByPassRecord = getUsageRecordWithId(lastBypassedId)
        if (lastByPassRecord == null) return false
        if (timeStamp - lastByPassRecord.endTimeStamp <= bypassExpireInterval * 1000) {
            val lastUsageRecord = getLastUsageRecord()
            if (lastUsageRecord == null) return false
            if (lastUsageRecord.id != lastByPassRecord.id) {
                lastBypassedId = lastUsageRecord.id
            }
            return true
        }
    }
    return false
}

suspend fun checkIntervene(
    metricPair: Pair<Double,Double>?,
    appList: List<MonitorApps>,
    lastUsageRecord : UsageRecord?,
    appPackageName: String,
    floatWindowProperty : FloatWindowProperty,
    timeStamp : Long
) {
    // if not in appList , stop control detection
    val appMatch = appList.find {appPackageName == it.appPackageName}
    if (appMatch == null) return
    // same appPackageName, do not intervene this time
    if (lastUsageRecord != null && lastUsageRecord.appPackageName == appPackageName) {
        return
    }
    // if controlOn is set to true, check if metric exceeds threshold
    val controlOn = getControlOn()
    if (!controlOn) return
    // if new metric exceeds threshold, intervene
    if (metricPair != null) {
        val (useTimeFactor,maxTimeFactor) = metricPair
        val eval = evaluator(useTimeFactor,maxTimeFactor)
        val threshold = getControlThreshold()
        // Here you may need to try update bypassTicket before verifying the condition
        if (eval >= threshold) {
            // check if lastLastBypassValid , if valid then bypass
            if (checkIfLastBypassValid(timeStamp)) return

            intervene(appMatch.appName,appPackageName,floatWindowProperty)
        }
    }
}

suspend fun intervene(
    triggeredAppName       : String,
    triggeredAppPackageName: String,
    floatWindowProperty: FloatWindowProperty
) {
    val method = getControlMethod()
    if (method.startsWith("OVERLAY_")) {
        floatWindowProperty.showWindow(
            triggeredAppName,
            triggeredAppPackageName
        )
    } else {
        // this branch should be for activities
        val intent = Intent(
            floatWindowProperty.accessibilityService,
            PromptActivity::class.java
        ).apply {
            putExtra("appName",triggeredAppName)
            putExtra("appPackageName",triggeredAppPackageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        floatWindowProperty.accessibilityService?.startActivity(intent)
    }
}

suspend fun setBypass() {
    val lastUsageRecord = getLastUsageRecord()
    if (lastUsageRecord == null) return
    lastBypassedIdLock.withLock {
        lastBypassedId = lastUsageRecord.id
    }
}
