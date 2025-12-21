package com.github.xingzheli.antibrainrot.ui.interfaces.intro

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry

data class LinkOption(
    val icon          : ImageVector,
    val tabName       : Int,
    val description   : Int,
    val route         : String,
)

val linkOptionList = listOf(
    LinkOption(
        icon    = Icons.Default.Book,
        tabName = R.string.tab_tutorial,
        description = R.string.tab_description_tutorial,
        route = RegistryEntry.TUTORIAL.route
    ),
    LinkOption(
        icon    = Icons.Default.GraphicEq,
        tabName = R.string.tab_status,
        description = R.string.tab_description_status,
        route = RegistryEntry.STATUS.route
    ),
    LinkOption(
        icon    = Icons.Default.Event,
        tabName = R.string.tab_record,
        description = R.string.tab_description_record,
        route = RegistryEntry.RECORD.route
    ),
    LinkOption(
        icon    = Icons.Default.Timeline,
        tabName = R.string.tab_metric,
        description = R.string.tab_description_metric,
        route = RegistryEntry.METRIC.route
    ),
    LinkOption(
        icon    = Icons.Default.Inventory,
        tabName = R.string.tab_control,
        description = R.string.tab_description_control,
        route = RegistryEntry.CONTROL.route
    ),
    LinkOption(
        icon    = Icons.Default.Settings,
        tabName = R.string.tab_settings,
        description = R.string.tab_description_settings,
        route = RegistryEntry.SETTINGS.route
    )
)