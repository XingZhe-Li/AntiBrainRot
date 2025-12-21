package com.github.xingzheli.antibrainrot.ui.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethodFlow
import com.github.xingzheli.antibrainrot.ui.overlays.block.Block
import com.github.xingzheli.antibrainrot.ui.overlays.calm.Calm
import com.github.xingzheli.antibrainrot.ui.theme.AppTheme

@Composable
fun OverlayEntry() {
    val method by getControlMethodFlow().collectAsStateWithLifecycle(CONTROL_METHOD_DEFAULT)

    AppTheme {
        methodMap[method]?.invoke()
    }
}

val methodMap = mapOf(
    "OVERLAY_BLOCK" to @Composable { Block() },
    "OVERLAY_CALM_DOWN" to @Composable { Calm() }
)