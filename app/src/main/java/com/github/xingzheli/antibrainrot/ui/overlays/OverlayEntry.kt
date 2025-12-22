package com.github.xingzheli.antibrainrot.ui.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_LANGUAGE_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethodFlow
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUiLanguage
import com.github.xingzheli.antibrainrot.ui.overlays.block.Block
import com.github.xingzheli.antibrainrot.ui.overlays.calm.Calm
import com.github.xingzheli.antibrainrot.ui.theme.AppTheme
import com.github.xingzheli.antibrainrot.utils.LocaleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun OverlayEntry() {
    val method by getControlMethodFlow().collectAsStateWithLifecycle(CONTROL_METHOD_DEFAULT)
    val originalContext = LocalContext.current
    var localizedContext by remember { mutableStateOf(originalContext) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val language = getUiLanguage()
            localizedContext = LocaleHelper.setLocale(originalContext, language)
        }
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        AppTheme {
            methodMap[method]?.invoke()
        }
    }
}

val methodMap = mapOf(
    "OVERLAY_BLOCK" to @Composable { Block() },
    "OVERLAY_CALM_DOWN" to @Composable { Calm() }
)