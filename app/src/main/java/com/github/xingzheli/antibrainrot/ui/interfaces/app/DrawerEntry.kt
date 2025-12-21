package com.github.xingzheli.antibrainrot.ui.interfaces.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry

enum class DrawerEntry (
    val route   : String,
    val tabName : String,
    val tabIcon : ImageVector,
) {
    INTRO (
        RegistryEntry.INTRO.route,
        "Introduction",
        Icons.Default.Code
    ),
    TUTORIAL (
        RegistryEntry.TUTORIAL.route,
        "Tutorial",
        Icons.Default.Book
    ),
    STATUS (
        RegistryEntry.STATUS.route,
        "Status",
        Icons.Default.GraphicEq
    ),
    RECORD (
        RegistryEntry.RECORD.route,
        "Record",
        Icons.Default.Event
    ),
    METRIC (
        RegistryEntry.METRIC.route,
        "Metric",
        Icons.Default.Timeline
    ),
    CONTROL (
        RegistryEntry.CONTROL.route,
        "Control",
        Icons.Default.Inventory
    ),
    SETTINGS (
        RegistryEntry.SETTINGS.route,
        "Settings",
        Icons.Default.Settings
    )
}