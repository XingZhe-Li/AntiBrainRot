package com.github.xingzheli.antibrainrot.data.datastore

import androidx.datastore.preferences.core.edit
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_FAST_START_HOURS_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_MAX_LOSS_RATE_PER_DAY_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_TRACK_ON_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_USAGE_LOSS_RATE_PER_DAY_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_CALM_TIME_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_ON_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_THRESHOLD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_DISPLAY_AT_LEAST_LENGTH_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_EXCLUDE_SYSTEM_APPS_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_LANGUAGE_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_USE_DRAWER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONFIG_FAST_START_HOURS
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONFIG_MAX_LOSS_RATE_PER_DAY
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONFIG_TRACK_ON
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONFIG_USAGE_LOSS_RATE_PER_DAY
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_BYPASS_EXPIRE_INTERVAL
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_CALM_TIME
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_MAX_TIME_FACTOR_MULTIPLIER
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_METHOD
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_ON
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_THRESHOLD
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.CONTROL_USE_TIME_FACTOR_MULTIPLIER
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.UI_DISPLAY_AT_LEAST_LENGTH
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.UI_EXCLUDE_SYSTEM_APPS
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.UI_LANGUAGE
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceKeys.UI_USE_DRAWER
import com.github.xingzheli.antibrainrot.shared.AppGlobals.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object PreferenceProxy {
    // This file is for PreferenceKey Proxies
    suspend fun getUIUseDrawer() : Boolean {
        return preferencesDataStore.data.first()[UI_USE_DRAWER] ?: UI_USE_DRAWER_DEFAULT
    }
    suspend fun setUIUseDrawer(option : Boolean) {
        preferencesDataStore.edit {
            it[UI_USE_DRAWER] = option
        }
    }

    suspend fun getExcludeSystemApps() : Boolean {
        return preferencesDataStore.data.first()[UI_EXCLUDE_SYSTEM_APPS] ?: UI_EXCLUDE_SYSTEM_APPS_DEFAULT
    }
    suspend fun setExcludeSystemApps(option : Boolean) {
        preferencesDataStore.edit {
            it[UI_EXCLUDE_SYSTEM_APPS] = option
        }
    }

    suspend fun getTrackOn() : Boolean {
        return preferencesDataStore.data.first()[CONFIG_TRACK_ON] ?: CONFIG_TRACK_ON_DEFAULT
    }
    suspend fun setTrackOn(option : Boolean) {
        preferencesDataStore.edit {
            it[CONFIG_TRACK_ON] = option
        }
    }

    suspend fun getUsageLossRatePerDay() : Float {
        return preferencesDataStore.data.first()[CONFIG_USAGE_LOSS_RATE_PER_DAY] ?: CONFIG_USAGE_LOSS_RATE_PER_DAY_DEFAULT
    }
    suspend fun setUsageLossRatePerDay(option : Float) {
        preferencesDataStore.edit {
            it[CONFIG_USAGE_LOSS_RATE_PER_DAY] = option
        }
    }

    suspend fun getMaxLossRatePerDay() : Float {
        return preferencesDataStore.data.first()[CONFIG_MAX_LOSS_RATE_PER_DAY] ?: CONFIG_MAX_LOSS_RATE_PER_DAY_DEFAULT
    }
    suspend fun setMaxLossRatePerDay(option : Float) {
        preferencesDataStore.edit {
            it[CONFIG_MAX_LOSS_RATE_PER_DAY] = option
        }
    }

    suspend fun getFastStartHours() : Float {
        return preferencesDataStore.data.first()[CONFIG_FAST_START_HOURS] ?: CONFIG_FAST_START_HOURS_DEFAULT
    }

    suspend fun setFastStartHours(fastStartHours : Float) {
        preferencesDataStore.edit {
            it[CONFIG_FAST_START_HOURS] = fastStartHours
        }
    }

    suspend fun getControlOn() : Boolean {
        return preferencesDataStore.data.first()[CONTROL_ON] ?: CONTROL_ON_DEFAULT
    }

    suspend fun setControlOn(option : Boolean) {
        preferencesDataStore.edit {
            it[CONTROL_ON] = option
        }
    }

    suspend fun getControlThreshold() : Float {
        return preferencesDataStore.data.first()[CONTROL_THRESHOLD] ?: CONTROL_THRESHOLD_DEFAULT
    }

    suspend fun setControlThreshold(threshold : Float) {
        preferencesDataStore.edit {
            it[CONTROL_THRESHOLD] = threshold
        }
    }

    suspend fun getControlUseTimeFactorMultiplier() : Float {
        return preferencesDataStore.data.first()[CONTROL_USE_TIME_FACTOR_MULTIPLIER] ?: CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
    }

    suspend fun setControlUseTimeFactorMultiplier(multiplier : Float) {
        preferencesDataStore.edit {
            it[CONTROL_USE_TIME_FACTOR_MULTIPLIER] = multiplier
        }
    }

    suspend fun getControlMaxTimeFactorMultiplier() : Float {
        return preferencesDataStore.data.first()[CONTROL_MAX_TIME_FACTOR_MULTIPLIER] ?: CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
    }

    suspend fun setControlMaxTimeFactorMultiplier(multiplier : Float) {
        preferencesDataStore.edit {
            it[CONTROL_MAX_TIME_FACTOR_MULTIPLIER] = multiplier
        }
    }

    fun getControlMethodFlow() : Flow<String> {
        return preferencesDataStore.data.map {
            it[CONTROL_METHOD] ?: CONTROL_METHOD_DEFAULT
        }
    }

    suspend fun getControlMethod() : String {
        return preferencesDataStore.data.first()[CONTROL_METHOD] ?: CONTROL_METHOD_DEFAULT
    }

    suspend fun setControlMethod(count : String) {
        preferencesDataStore.edit {
            it[CONTROL_METHOD] = count
        }
    }

    suspend fun getControlCalmTime() : Int {
        return preferencesDataStore.data.first()[CONTROL_CALM_TIME] ?: CONTROL_CALM_TIME_DEFAULT
    }

    suspend fun setControlCalmTime(count : Int) {
        preferencesDataStore.edit {
            it[CONTROL_CALM_TIME] = count
        }
    }

    suspend fun getControlBypassExpireInterval() : Int {
        return preferencesDataStore.data.first()[CONTROL_BYPASS_EXPIRE_INTERVAL] ?: CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT
    }

    suspend fun setControlBypassExpireInterval(interval : Int) {
        preferencesDataStore.edit {
            it[CONTROL_BYPASS_EXPIRE_INTERVAL] = interval
        }
    }

    suspend fun getUiDisplayAtLeastLength() : Long {
        return preferencesDataStore.data.first()[UI_DISPLAY_AT_LEAST_LENGTH] ?: UI_DISPLAY_AT_LEAST_LENGTH_DEFAULT
    }

    suspend fun setUiDisplayAtLeastLength(len : Long) {
        preferencesDataStore.edit {
            it[UI_DISPLAY_AT_LEAST_LENGTH] = len
        }
    }

    suspend fun getUiLanguage() : String {
        return preferencesDataStore.data.first()[UI_LANGUAGE] ?: UI_LANGUAGE_DEFAULT
    }

    suspend fun setUiLanguage(language : String) {
        preferencesDataStore.edit {
            it[UI_LANGUAGE] = language
        }
    }
}