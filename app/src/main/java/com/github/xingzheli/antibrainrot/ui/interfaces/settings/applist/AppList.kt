package com.github.xingzheli.antibrainrot.ui.interfaces.settings.applist

import android.util.MutableInt
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.Accessor.getMonitorAppsFlow
import com.github.xingzheli.antibrainrot.data.Accessor.insertMonitorApp
import com.github.xingzheli.antibrainrot.data.Accessor.removeMonitorAppWithPackageName
import com.github.xingzheli.antibrainrot.data.room.MonitorApps
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.utils.AppListEntry
import com.github.xingzheli.antibrainrot.utils.getInstalledAppList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

data class FilterMode(
    val icon : ImageVector,
    val filterFunc : (AppListEntry,List<MonitorApps>) -> Boolean
)

val LocalFilterModePtr = compositionLocalOf<MutableIntState> {
    error("LocalFilterModePtr not provided")
}

val filterModes = listOf(
    FilterMode (
        icon = Icons.AutoMirrored.Filled.List,
        filterFunc = { appListEntry , monitorApps -> true }
    ),
    FilterMode (
        icon = Icons.Default.FilterList,
        filterFunc = { appListEntry , monitorApps -> appListEntry.appPackageName in monitorApps.map { it.appPackageName } }
    ),
    FilterMode (
        icon = Icons.Default.FilterListOff,
        filterFunc = { appListEntry , monitorApps -> appListEntry.appPackageName !in monitorApps.map { it.appPackageName } }
    )
)

@Composable
fun AppListTopbar(scrollState: ScrollState) {
    val titleAlpha = min(
        max(0.0f,(scrollState.value - 140) / 240.0f),
        1.0f
    )
    val navHostController = LocalNavHostController.current
    val filterModePtr = LocalFilterModePtr.current

    Row (
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp,0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick  = {
                navHostController.popBackStack()
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.general_back_button)
            )
        }

        Text(
            text = stringResource(R.string.screen_title_app_list),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .alpha(titleAlpha),
            textAlign = TextAlign.Center
        )

        Spacer(
            Modifier.weight(1f)
        )

        IconButton(
            {
                filterModePtr.intValue = (filterModePtr.intValue + 1) % filterModes.size
            }
        ) {
            Icon(
                filterModes[filterModePtr.intValue].icon,
                contentDescription = null
            )
        }
    }
}

@Composable
fun AppListItem(appListEntry : AppListEntry, monitorList : List<MonitorApps>) {
    val coroutineScope = rememberCoroutineScope()

    val inMonitor = appListEntry.appPackageName in monitorList.map {
        it.appPackageName
    }

    Row (
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable{
                if (inMonitor) {
                    coroutineScope.launch {
                        removeMonitorAppWithPackageName (
                            appListEntry.appPackageName
                        )
                    }
                } else {
                    coroutineScope.launch {
                        insertMonitorApp(
                            MonitorApps(
                                appListEntry.appPackageName,
                                appListEntry.appName
                            )
                        )
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            appListEntry.iconBitmap,
            contentDescription = appListEntry.appName,
            modifier = Modifier.fillMaxHeight().padding(16.dp)
        )

        Column (
            Modifier
                .padding(0.dp,16.dp)
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text (
                appListEntry.appName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                appListEntry.appPackageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Crossfade(
            inMonitor
        ) {
            if (it) {
                Icon(
                    Icons.Default.CheckBox,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Icon(
                    Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun AppListItemsContainer() {
    var appList by remember { mutableStateOf<List<AppListEntry>>(listOf()) }
    val monitorList by getMonitorAppsFlow().collectAsStateWithLifecycle(listOf())
    var loaded by remember { mutableStateOf(false) }

    val filterModePtr = LocalFilterModePtr.current
    val filterMode = filterModes[filterModePtr.intValue]

    val filteredAppList = appList.filter {
        filterMode.filterFunc(it,monitorList)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            appList = getInstalledAppList()
            loaded = true
        }
    }

    Box (
        Modifier
            .fillMaxWidth()
            .padding(24.dp,0.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        if (loaded) {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.spacedBy(2.dp)
            ) {
                for (appListEntry in filteredAppList) {
                    AppListItem(appListEntry,monitorList)
                }
            }
        } else {
            Box (
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}

@Composable
fun AppListContainer(scrollState: ScrollState) {
    val titleAlpha = min(
        max(0.0f,(180 - scrollState.value) / 100.0f),
        1.0f
    )

    Column (
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box (
            Modifier
                .padding(24.dp,0.dp)
                .height(100.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = stringResource(R.string.screen_title_app_list),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.alpha(titleAlpha)
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(24.dp)
        )
        AppListItemsContainer()
    }
}

@Composable
fun AppList() {
    val scrollState = rememberScrollState()
    val filterModePtr = remember { mutableIntStateOf(0) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        CompositionLocalProvider(
            LocalFilterModePtr provides filterModePtr
        ) {
            AppListTopbar(scrollState)
            AppListContainer(scrollState)
        }
    }
}