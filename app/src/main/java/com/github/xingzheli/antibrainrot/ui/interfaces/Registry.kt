package com.github.xingzheli.antibrainrot.ui.interfaces

import ControlRecord
import androidx.compose.runtime.Composable
import com.github.xingzheli.antibrainrot.ui.interfaces.intro.Intro
import com.github.xingzheli.antibrainrot.ui.interfaces.metric.Metric
import com.github.xingzheli.antibrainrot.ui.interfaces.record.Record
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.applist.AppList
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.Settings
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.about.About
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.config.Config
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.control.Control
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.ui.UISettings
import com.github.xingzheli.antibrainrot.ui.interfaces.status.Status
import com.github.xingzheli.antibrainrot.ui.interfaces.tutorial.Tutorial
import com.github.xingzheli.antibrainrot.ui.interfaces.util.Loading

enum class RegistryEntry(
    val route   : String,
    val content : @Composable ()->Unit
) {
    INTRO    ("intro" ,{ Intro() } ),
    STATUS   ("status",{ Status() } ),
    RECORD   ("record",{ Record() } ),
    METRIC   ("metric",{ Metric() } ),
    CONTROL  ("control", { ControlRecord() }),
    TUTORIAL ("tutorial", { Tutorial() }),
    SETTINGS ("settings" ,{ Settings() } ),
    SETTINGS_APP_LIST ("settings/appList" ,{ AppList() } ),
    SETTINGS_UI ("settings/ui" ,{ UISettings() } ),
    SETTINGS_CONFIG ("settings/config" ,{ Config() } ),
    SETTINGS_CONTROL ("settings/control" ,{ Control() } ),
    SETTINGS_ABOUT ("settings/about" ,{ About() } ),
    LOADING ("loading", { Loading() } )
}

val startRegistry = RegistryEntry.INTRO