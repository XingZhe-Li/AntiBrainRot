package com.github.xingzheli.antibrainrot.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val CONFIG_TRACK_ON         = booleanPreferencesKey("config_track_on")
    val UI_USE_DRAWER           = booleanPreferencesKey("ui_use_drawer")
    val UI_EXCLUDE_SYSTEM_APPS  = booleanPreferencesKey("ui_exclude_system_apps")
    val UI_DISPLAY_AT_LEAST_LENGTH =
        longPreferencesKey("ui_display_at_least_length")
    val CONFIG_USAGE_LOSS_RATE_PER_DAY =
        floatPreferencesKey(name = "config_usage_loss_rate_per_day")
    val CONFIG_MAX_LOSS_RATE_PER_DAY =
        floatPreferencesKey("config_max_loss_rate_per_day")
    val CONFIG_FAST_START_HOURS =
        floatPreferencesKey("config_fast_start_hours")
    val CONTROL_ON =
        booleanPreferencesKey("control_on")
    val CONTROL_THRESHOLD =
        floatPreferencesKey("control_threshold")
    val CONTROL_USE_TIME_FACTOR_MULTIPLIER =
        floatPreferencesKey("control_use_time_factor_multiplier")
    val CONTROL_MAX_TIME_FACTOR_MULTIPLIER =
        floatPreferencesKey("control_max_time_factor_multiplier")
    val CONTROL_METHOD =
        stringPreferencesKey("control_method")
    val CONTROL_CALM_TIME =
        intPreferencesKey("control_calm_time")
    val CONTROL_BYPASS_EXPIRE_INTERVAL =
        intPreferencesKey("control_bypass_expire_interval")
}