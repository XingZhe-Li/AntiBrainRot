package com.github.xingzheli.antibrainrot.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getExcludeSystemApps
import com.github.xingzheli.antibrainrot.shared.AppGlobals

data class AppListEntry (
    val appName : String,
    val appPackageName : String,
    val iconBitmap: ImageBitmap
)

@SuppressLint("QueryPermissionsNeeded")
suspend fun getInstalledAppList(): List<AppListEntry> {
    val context = AppGlobals.application
    val packageManager: PackageManager = context.packageManager
    val excludeSystemApps = getExcludeSystemApps()

    val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    val launcherIntentFilter = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    // Keep Launchable Apps , filter out services
    var filteredApps = apps
        .filter { appInfo ->
            packageManager.resolveActivity(launcherIntentFilter.apply {
                setPackage(appInfo.packageName)
            }, 0) != null
        }

    if (excludeSystemApps) {
        filteredApps = filteredApps.filter { appInfo ->
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        }
    }

    return filteredApps
        .mapNotNull { appInfo ->
            try {
                val appName = appInfo.loadLabel(packageManager).toString()
                val packageName = appInfo.packageName

                val drawable = appInfo.loadIcon(packageManager)
                val bitmap = drawable.toBitmap(
                    width = 128,
                    height = 128,
                    config = Bitmap.Config.ARGB_8888
                )
                val imageBitmap = bitmap.asImageBitmap()

                AppListEntry(
                    appName = appName,
                    appPackageName = packageName,
                    iconBitmap = imageBitmap
                )
            } catch (e: Exception) {
                null
            }
        }
}