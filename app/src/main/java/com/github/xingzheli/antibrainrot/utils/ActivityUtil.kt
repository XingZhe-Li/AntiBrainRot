package com.github.xingzheli.antibrainrot.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.xingzheli.antibrainrot.shared.AppGlobals

fun startActivity(packageName : String) {
    val appContext   = AppGlobals.application
    val launchIntent = appContext.packageManager.getLaunchIntentForPackage(packageName)

    launchIntent?.apply {
        // 依然需要添加 NEW_TASK 标志，因为是从 Service 启动
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 可选：如果 Activity 已经在运行，则将其唤醒到最前台
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    appContext.startActivity(launchIntent)
}

fun backToHome() {
    val appContext   = AppGlobals.application
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    appContext.startActivity(intent)
}

fun finishActivity(activityContext : Context) {
    (activityContext as Activity).finish()
}