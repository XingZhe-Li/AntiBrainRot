/*
    AntiBrainRot for Tracking & Controlling AppUsage
    Copyright (C) 2025 XingZhe-Li

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/

package com.github.xingzheli.antibrainrot

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.github.xingzheli.antibrainrot.core.tracker.onEvent
import com.github.xingzheli.antibrainrot.core.tracker.onPause
import com.github.xingzheli.antibrainrot.utils.FloatWindowProperty
import com.github.xingzheli.antibrainrot.utils.createFloatWindow

@SuppressLint("AccessibilityPolicy")
class MainAccessibilityService : AccessibilityService(){

    lateinit var floatWindowProperty : FloatWindowProperty

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onServiceConnected() {
        super.onServiceConnected()
        floatWindowProperty = createFloatWindow(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        floatWindowProperty.destroyFloatWindow()
        onPause()
        return super.onUnbind(intent)
    }

    // Monitoring Logic
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        onEvent(event,floatWindowProperty)
    }
    override fun onInterrupt() {}
}