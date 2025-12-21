package com.github.xingzheli.antibrainrot.ui.interfaces.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewArray
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry

data class SettingOption (
    val tabName : Int,
    val icon    : ImageVector,
    val route   : String,
    val description : Int
)


val optionList = listOf(
    SettingOption (
        R.string.settings_option_app_list,
        Icons.Default.Checklist,
        RegistryEntry.SETTINGS_APP_LIST.route,
        R.string.settings_option_app_list_description
    ),
    SettingOption (
        R.string.settings_option_ui,
        Icons.Default.ViewArray,
        RegistryEntry.SETTINGS_UI.route,
        R.string.settings_option_ui_description
    ),
    SettingOption (
        R.string.settings_option_config,
        Icons.Default.Tune,
        RegistryEntry.SETTINGS_CONFIG.route,
        R.string.settings_option_config_description
    ),
    SettingOption (
        R.string.settings_option_control,
        Icons.Default.TrackChanges,
        RegistryEntry.SETTINGS_CONTROL.route,
        R.string.settings_option_control_description
    ),
    SettingOption (
        R.string.settings_option_about,
        Icons.Default.Info,
        RegistryEntry.SETTINGS_ABOUT.route,
        R.string.settings_option_about_description
    )
)