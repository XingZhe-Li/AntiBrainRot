package com.github.xingzheli.antibrainrot.utils

import android.accessibilityservice.AccessibilityService
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.github.xingzheli.antibrainrot.shared.AppGlobals
import com.github.xingzheli.antibrainrot.ui.overlays.OverlayEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Honestly , this should be called WindowHandle rather than property
data class FloatWindowProperty (
    var lifecycleOwner : MyLifecycleOwner?           = null,
    var windowManager  : WindowManager?              = null,
    var entryView      : View?                       = null,
    var layoutParams   : WindowManager.LayoutParams? = null,
    var visible        : MutableState<Boolean> = mutableStateOf(false),
    var accessibilityService : AccessibilityService? = null,
    var triggeredAppPackageName : String             = "",
    var triggeredAppName        : String             = ""
) {
    suspend fun showWindow(argTriggeredAppName: String = "",argTriggeredAppPackageName : String = "") {
        if (visible.value) return
        windowManager?.let { wm ->
            entryView?.let { view ->
                try {
                    withContext(Dispatchers.Main) {
                        wm.addView(view,layoutParams)
                        visible.value = true
                        triggeredAppPackageName = argTriggeredAppPackageName
                        triggeredAppName = argTriggeredAppName
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun hideWindow() {
        withContext(Dispatchers.Main) {
            if (!visible.value) return@withContext
            windowManager?.removeView(entryView)
            visible.value = false
        }
    }

    fun destroyFloatWindow() {
        lifecycleOwner?.onPause()
        lifecycleOwner?.onStop()
        if (visible.value) {
            windowManager?.removeView(entryView)
            visible.value = false
        }
        lifecycleOwner?.onDestroy()
    }
}

val LocalFloatWindowProperty = compositionLocalOf<FloatWindowProperty> {
    error("No FloatWindowProperty provided")
}

@RequiresApi(Build.VERSION_CODES.R)
fun createFloatWindow(
    accessibilityService : AccessibilityService,
) : FloatWindowProperty {
    val applicationContext = AppGlobals.application

    val windowProperty = FloatWindowProperty()
    windowProperty.accessibilityService = accessibilityService

    windowProperty.apply {
        lifecycleOwner = MyLifecycleOwner()
        windowManager  = accessibilityService.getSystemService(WINDOW_SERVICE) as? WindowManager

        entryView      = ComposeView(applicationContext).apply {
            lifecycleOwner?.attachToView(this)
            lifecycleOwner?.performRestore(null)
            lifecycleOwner?.onCreate()
            lifecycleOwner?.onStart()
            lifecycleOwner?.onResume()
            setContent {
                CompositionLocalProvider(
                    LocalFloatWindowProperty provides windowProperty
                ) {
                    OverlayEntry()
                }
            }
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }
    }

    return windowProperty
}