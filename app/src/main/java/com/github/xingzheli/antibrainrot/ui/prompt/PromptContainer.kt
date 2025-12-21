package com.github.xingzheli.antibrainrot.ui.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethodFlow
import com.github.xingzheli.antibrainrot.ui.prompt.block.Block
import com.github.xingzheli.antibrainrot.ui.prompt.calm.Calm
import com.github.xingzheli.antibrainrot.ui.prompt.prompt.Prompt
import com.github.xingzheli.antibrainrot.ui.theme.AppTheme

val LocalAppPackageName = compositionLocalOf<String> {
    error("LocalAppPackageName not provided")
}

val LocalAppName = compositionLocalOf<String> {
    error("LocalAppPackageName not provided")
}

@Composable
fun PromptContainer(appName: String,appPackageName: String) {
    val method by getControlMethodFlow().collectAsStateWithLifecycle(CONTROL_METHOD_DEFAULT)

    CompositionLocalProvider(
        LocalAppPackageName provides appPackageName,
        LocalAppName provides appName
    ) {
        AppTheme {
            Scaffold {
                Box (
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    methodMap[method]?.invoke()
                }
            }
        }
    }
}

val methodMap = mapOf(
    "ACTIVITY_BLOCK" to @Composable { Block() },
    "ACTIVITY_CALM_DOWN"  to @Composable { Calm()  },
    "ACTIVITY_PROMPT" to @Composable { Prompt() }
)