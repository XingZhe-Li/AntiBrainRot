package com.github.xingzheli.antibrainrot.ui.interfaces.metric

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.Accessor.getMetricRecordWithRange
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_THRESHOLD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.github.xingzheli.antibrainrot.ui.interfaces.components.DatePickerModal
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.listOf

fun getTodayMillisecondRange(): Pair<Long, Long> {
    val today = LocalDate.now() // Using Default Time Zone
    val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return startOfDay to endOfDay
}


fun formatTimestamp(timestampMs: Long): String {
    val instant = Instant.ofEpochMilli(timestampMs)
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun formatMillisRangeToDateString(
    dateRange: Pair<Long,Long>,
    zoneId: ZoneId = ZoneId.systemDefault()
): Pair<String, String> {
    val (startMillis,endMillis) = dateRange
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
    val startStr = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate().format(formatter)
    val endStr = Instant.ofEpochMilli(endMillis - 1).atZone(zoneId).toLocalDate().format(formatter)
    return startStr to endStr
}

fun formatDurationMs(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("${hours}h")
        if (minutes > 0) append("${minutes}m")
        if (seconds > 0 || (hours == 0L && minutes == 0L)) append("${seconds}s") // 至少显示 0s 当全为0时
    }
}

val LocalShowDatePicker = compositionLocalOf<MutableState<Boolean>> {
    error("showDatePicker not provided")
}

val LocalDateRange = compositionLocalOf<MutableState<Pair<Long,Long>>> {
    error("showDatePicker not provided")
}

val LocalDatePickerCallback = compositionLocalOf<MutableState<(Long?)->Unit>> {
    error("datePickerCallback not provided")
}

@Composable
fun MetricTopBar() {
    val navHostController = LocalNavHostController.current

    Row (
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp,0.dp),
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
            text = stringResource(R.string.screen_title_metric),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricDatePicker() {
    val showDatePicker = LocalShowDatePicker.current
    val dateRange = LocalDateRange.current
    val datePickerCallback = LocalDatePickerCallback.current

    val (startDateString,endDateString) = formatMillisRangeToDateString(dateRange.value)

    Row (
        Modifier
            .fillMaxWidth()
            .padding(24.dp,8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp,16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.DateRange,
            contentDescription = null
        )

        Text(
            startDateString,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
                datePickerCallback.value = {
                    if (it != null) {
                        dateRange.value = dateRange.value.copy(
                            first = it
                        )
                    }
                }
                showDatePicker.value = true
            }
        )

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null
        )

        Text(
            endDateString,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
                datePickerCallback.value = {
                    if (it != null) {
                        dateRange.value = dateRange.value.copy(
                            second = it + 1000 * 24 * 60 * 60
                        )
                    }
                }
                showDatePicker.value = true
            }
        )
    }
}

@Composable
fun MetricContent() {
    val dateRange = LocalDateRange.current
    val displayOptions = remember {
        mutableStateOf(listOf("useTimeFactor"))
    }
    val metricList = remember {
        mutableStateOf(listOf<Metric>())
    }

    val useTimeFactorMultiplier = remember { mutableFloatStateOf(CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT) }
    val maxTimeFactorMultiplier = remember { mutableFloatStateOf(CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT) }
    val threshold = remember { mutableFloatStateOf(CONTROL_THRESHOLD_DEFAULT) }

    LaunchedEffect(dateRange.value) {
        withContext(Dispatchers.IO) {
            val (startTimeStamp, endTimeStamp) = dateRange.value
            metricList.value = getMetricRecordWithRange(startTimeStamp, endTimeStamp)
        }
    }

    CompositionLocalProvider(
        LocalDisplayOptions provides displayOptions,
        LocalUseTimeFactorMultiplier provides useTimeFactorMultiplier,
        LocalMaxTimeFactorMultiplier provides maxTimeFactorMultiplier,
        LocalThreshold provides threshold
    ) {
        Column (
            Modifier.fillMaxSize()
        ) {
            MetricDatePicker()
            Box(
                Modifier.weight(1f)
            ) {
                MetricGraph(metricList.value)
            }
            MetricOptions()
            Spacer(
                Modifier.height(24.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
fun Metric() {
    val showDatePicker = remember { mutableStateOf(false) }
    val dateRange = remember { mutableStateOf(getTodayMillisecondRange()) }
    val datePickerCallback: MutableState<(Long?) -> Unit> = remember { mutableStateOf({}) }

    CompositionLocalProvider(
        LocalShowDatePicker provides showDatePicker,
        LocalDateRange provides dateRange,
        LocalDatePickerCallback provides datePickerCallback,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            MetricTopBar()
            MetricContent()
        }
    }

    if (showDatePicker.value) {
        DatePickerModal(
            onDateSelected = datePickerCallback.value,
            onDismiss = {showDatePicker.value = false}
        )
    }
}