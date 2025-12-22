package com.github.xingzheli.antibrainrot.data.datastore

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat

object PreferenceDefaults {
    const val CONFIG_TRACK_ON_DEFAULT                     = true
    const val UI_USE_DRAWER_DEFAULT                       = false
    const val UI_EXCLUDE_SYSTEM_APPS_DEFAULT              = true
    const val UI_DISPLAY_AT_LEAST_LENGTH_DEFAULT          = 1000L
    const val CONFIG_USAGE_LOSS_RATE_PER_DAY_DEFAULT      = 0.8f
    const val CONFIG_MAX_LOSS_RATE_PER_DAY_DEFAULT        = 0.8f
    const val CONFIG_FAST_START_HOURS_DEFAULT             = 24f
    const val CONTROL_ON_DEFAULT                          = false
    const val CONTROL_THRESHOLD_DEFAULT                   = 0.0f
    const val CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT  = 24f * 60 * 60
    const val CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT  = 1f
    const val CONTROL_METHOD_DEFAULT  = "ACTIVITY_BLOCK" // {"PURPOSE_QUEST","BLOCK","CALM_DOWN","OPTION_QUEST"}
    val CONTROL_METHOD_DEFAULT_LIST  = listOf(
        "OVERLAY_BLOCK",
        "OVERLAY_CALM_DOWN",
        "ACTIVITY_BLOCK",
        "ACTIVITY_CALM_DOWN",
        "ACTIVITY_PROMPT"
    )
    const val CONTROL_CALM_TIME_DEFAULT = 20
    const val CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT = 10

    val UI_LANGUAGE_DEFAULT : String
        get() = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]!!.language
    val UI_LANGUAGE_DEFAULT_LIST = listOf("en","zh")
}