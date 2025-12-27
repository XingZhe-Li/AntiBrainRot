package com.github.xingzheli.antibrainrot.core.tracker

import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMaxTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlUseTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getFastStartHours
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getMaxLossRatePerDay
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUsageLossRatePerDay
import com.github.xingzheli.antibrainrot.shared.RuntimeConfig.useTimeFactorMultiplier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

suspend fun accelerateFactor(totalUsedTime: Long) : Double {
    val trackerAccelerateTimeLen = getFastStartHours().toDouble() * 60 * 60 * 1000
    val x = (totalUsedTime.toDouble()) / trackerAccelerateTimeLen
    return max(
        min(2 * (- 1 / (x + 1) + 1),1.0),
    0.0)
}

suspend fun updateUseTimeFactor(
    usingInt         : Long,
    oldUseTimeFactor : Double,
    startUsedTime    : Long,
    usedTimeIncrement: Long
) : Double {
    val MS_OF_A_DAY = 1000 * 24 * 60 * 60 * 1.0
    val usageLossRatePerDay = getUsageLossRatePerDay()
    val keepRateWithFastStart =
        usageLossRatePerDay * accelerateFactor(startUsedTime)
    val timedKeepRateWithFastStart =
        keepRateWithFastStart.pow(usedTimeIncrement / MS_OF_A_DAY)

    return oldUseTimeFactor * timedKeepRateWithFastStart + usingInt * (1 - timedKeepRateWithFastStart)
}

suspend fun updateMaxTimeFactor(
    timeInDebt       : Double,
    oldMaxTimeFactor : Double,
    startUsedTime    : Long,
    usedTimeIncrement: Long
) : Double {
    val MS_OF_A_DAY = 1000 * 24 * 60 * 60 * 1.0
    val maxLossRatePerDay = getMaxLossRatePerDay()
    val keepRateWithFastStart =
        maxLossRatePerDay * accelerateFactor(startUsedTime)
    val timedKeepRateWithFastStart =
        keepRateWithFastStart.pow(usedTimeIncrement / MS_OF_A_DAY)

    return sqrt(timedKeepRateWithFastStart * (oldMaxTimeFactor * oldMaxTimeFactor) + (1 - timedKeepRateWithFastStart) * (timeInDebt * timeInDebt))
}

suspend fun updateTimeInDebt (
    usingInt          : Long,
    oldTimeInDebt     : Double,
    usedTimeIncrement : Long,
    useTimeFactor     : Double
) : Double {
    val realUseTimeFactor = max(min(
        useTimeFactorMultiplier * useTimeFactor,
        1.0
    ),0.0)

    return if (usingInt == 1L) {
        max(
            oldTimeInDebt + usedTimeIncrement / 1000.0  * (1 - realUseTimeFactor),
            0.0
        )
    } else {
        max(
            oldTimeInDebt - usedTimeIncrement / 1000.0 * realUseTimeFactor,
            0.0
        )
    }
}

suspend fun evaluator(useTimeFactor: Double,maxTimeFactor : Double) : Double{
    val useTimeFactorMultiplier = getControlUseTimeFactorMultiplier()
    val maxTimeFactorMultiplier = getControlMaxTimeFactorMultiplier()
    return useTimeFactor * useTimeFactorMultiplier + maxTimeFactor * maxTimeFactorMultiplier
}