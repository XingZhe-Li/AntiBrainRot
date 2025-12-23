package com.github.xingzheli.antibrainrot.ui.interfaces.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.utils.isAccessibilityServiceEnabled
import com.github.xingzheli.antibrainrot.utils.isUsageStatsPermissionGranted
import com.github.xingzheli.antibrainrot.utils.openAccessibilitySettings
import com.github.xingzheli.antibrainrot.utils.openUsageStatsSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun TutorialTopbar() {
    val navController = LocalNavHostController.current

    Row (
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
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
                contentDescription = stringResource(R.string.tutorial_back_button)
            )
        }

        Text(
            text = stringResource(R.string.tutorial_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TutorialContentContainer() {
    val scrollState = rememberScrollState()

    Column (
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        TutorialContent()
    }
}

@Composable
fun WhyDoIBuildThisApp() {
    Spacer(Modifier.height(8.dp))

    Text(
        stringResource(R.string.tutorial_why_build_app_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text("    " + stringResource(R.string.tutorial_why_build_app_paragraph_1))
    Text("    " + stringResource(R.string.tutorial_why_build_app_paragraph_2))
    Text("    " + stringResource(R.string.tutorial_why_build_app_paragraph_3))
    Text("    " + stringResource(R.string.tutorial_why_build_app_paragraph_4))
    Text("    " + stringResource(R.string.tutorial_why_build_app_paragraph_5))

    Spacer(Modifier.height(8.dp))
}

@Composable
fun WhatAreTheseMetrics() {
    val image = painterResource(R.drawable.leaky_bucket)

    Spacer(Modifier
        .fillMaxWidth()
        .height(8.dp))

    Text(
        stringResource(R.string.tutorial_metrics_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_1))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_2))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_3))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_4))

    Column (
        Modifier
            .padding(8.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text(
            stringResource(R.string.tutorial_leaky_bucket_model),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_5))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_6))
    Text("        " + stringResource(R.string.tutorial_use_time_factor))
    Text("        " + stringResource(R.string.tutorial_max_time_factor))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_7))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_8))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_9))
    Text("    " + stringResource(R.string.tutorial_metrics_paragraph_10))

    Spacer(Modifier.height(8.dp))
}

@Composable
fun RequestForPermission() {
    var accessibilityEnabled by remember { mutableStateOf(false) }
    val checkIcon = if (accessibilityEnabled) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked
    val activityContext = LocalContext.current
    val serviceName = "com.github.xingzheli.antibrainrot/.MainAccessibilityService"

    val gotoSettings = {
        openAccessibilitySettings(activityContext)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (true) {
                accessibilityEnabled = isAccessibilityServiceEnabled(activityContext,serviceName)
                delay(3000)
            }
        }
    }

    Text(
        stringResource(R.string.tutorial_request_for_permission_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text(stringResource(R.string.tutorial_request_for_permission_paragraph1))
    Text(stringResource(R.string.tutorial_request_for_permission_paragraph2))

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = gotoSettings)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            checkIcon,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            if (accessibilityEnabled) stringResource(R.string.tutorial_request_for_permission_enabled) else stringResource(
                R.string.tutorial_request_for_permission_disabled
            ),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun TutorialContent() {
    RequestForPermission()
    WhyDoIBuildThisApp()
    WhatAreTheseMetrics()
    HowToControl()
    AppUsageBasedTracking()
}

@Composable
fun HowToControl() {
    Spacer(Modifier.height(8.dp))

    Text(
        stringResource(R.string.tutorial_how_to_control_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text("    " + stringResource(R.string.tutorial_how_to_control_paragraph_1))
    Text("    " + stringResource(R.string.tutorial_how_to_control_paragraph_2))
    Text("    " + stringResource(R.string.tutorial_how_to_control_paragraph_3))
}

@Composable
fun AppUsageBasedTracking() {
    var usageStatsPermissionGranted by remember { mutableStateOf(false) }
    val checkIcon = if (usageStatsPermissionGranted) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked
    val activityContext = LocalContext.current

    val gotoSettings = {
        openUsageStatsSettings(activityContext)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (true) {
                usageStatsPermissionGranted = isUsageStatsPermissionGranted(activityContext)
                delay(3000)
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    Text(
        stringResource(R.string.tutorial_usage_events_based_tracking),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text("    " + stringResource(R.string.tutorial_usage_events_based_tracking_paragraph1))
    Text("    " + stringResource(R.string.tutorial_usage_events_based_tracking_paragraph2))
    Text("    " + stringResource(R.string.tutorial_usage_events_based_tracking_paragraph3))

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = gotoSettings)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            checkIcon,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            if (usageStatsPermissionGranted) stringResource(R.string.tutorial_usageStats_permission_granted) else stringResource(
                R.string.tutorial_usageStats_permission_not_granted
            ),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun Tutorial() {
    Column (
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        TutorialTopbar()
        TutorialContentContainer()
    }
}
