package com.github.xingzheli.antibrainrot.ui.interfaces.status

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.core.tracker.accelerateFactor
import com.github.xingzheli.antibrainrot.core.tracker.evaluator
import com.github.xingzheli.antibrainrot.data.Accessor.getLastMetricRecord
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getConfigTrackMethod
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StatusTopBar() {
    val navHostController = LocalNavHostController.current

    Row (
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            text = stringResource(R.string.screen_title_status),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatusHeaderIcon() {
    Column (
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon (
            Icons.Default.GraphicEq,
            contentDescription = null,
            modifier = Modifier
                .height(96.dp)
                .width(96.dp)
        )

        Text (
            stringResource(R.string.screen_title_status),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun StatusCurrentMetrics() {
    val lastMetricRecord = remember { mutableStateOf<Metric?>(null) }

    val formattedUseTimeFactor = if (lastMetricRecord.value == null) {
        "not available"
    } else {
        String.format("%.4f", lastMetricRecord.value!!.useTimeFactor)
    }
    val formattedMaxTimeFactor = if (lastMetricRecord.value == null) {
        "not available"
    } else {
        String.format("%.2f", lastMetricRecord.value!!.maxTimeFactor)
    }
    var formattedEvaluator by remember { mutableStateOf("not available") }
    var formattedFastStartMultiplier by remember { mutableStateOf("not available") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            lastMetricRecord.value = getLastMetricRecord()

            if (lastMetricRecord.value == null) return@withContext
            formattedEvaluator = String.format(
                "%.2f",
                evaluator(
                    lastMetricRecord.value!!.useTimeFactor,
                    lastMetricRecord.value!!.maxTimeFactor
                )
            )
            formattedFastStartMultiplier = String.format(
                "%.2f",
                accelerateFactor(
                    lastMetricRecord.value!!.totalUsedTime
                )
            )
        }
    }

    Box (
        Modifier
            .padding(24.dp, 0.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.status_use_time_factor_label),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                formattedUseTimeFactor,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.status_max_time_factor_label),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    stringResource(R.string.status_max_time_factor_unit),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                formattedMaxTimeFactor,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(R.string.status_evaluator_label),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                formattedEvaluator,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(R.string.status_fast_start_multiplier_label),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                formattedFastStartMultiplier,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusContentContainer() {
    val scrollState = rememberScrollState()
    var showUpdater by remember { mutableStateOf(false) }
    val navHostController = LocalNavHostController.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        showUpdater = (getConfigTrackMethod() == "appUsageEvent")
    }

    Column (
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        StatusHeaderIcon()
        StatusCurrentMetrics()

        if (showUpdater) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable {
                        coroutineScope.launch {
                            navHostController.navigate(
                                RegistryEntry.LOADING.route
                            )
                        }
                    }
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.appusageevents_update),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun Status() {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        StatusTopBar()
        StatusContentContainer()
    }
}