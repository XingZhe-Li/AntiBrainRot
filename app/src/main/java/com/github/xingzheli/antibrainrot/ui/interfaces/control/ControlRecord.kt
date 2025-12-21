import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.github.xingzheli.antibrainrot.data.Accessor.getControlRecordWithRange
import com.github.xingzheli.antibrainrot.data.Accessor.getUsageRecordWithRange
import com.github.xingzheli.antibrainrot.data.room.ControlRecord
import com.github.xingzheli.antibrainrot.data.room.UsageRecord
import com.github.xingzheli.antibrainrot.ui.interfaces.components.DatePickerModal
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
fun ControlRecord() {
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
            ControlRecordTopBar()
            ControlRecordContent()
        }
    }

    if (showDatePicker.value) {
        DatePickerModal(
            onDateSelected = datePickerCallback.value,
            onDismiss = {showDatePicker.value = false}
        )
    }
}

@Composable
fun ControlRecordDatePicker() {
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
fun ControlRecordListItem(controlRecord : ControlRecord) {
    val timeString = formatTimestamp(controlRecord.timeStamp)

    Column (
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        Text(
            controlRecord.appName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            controlRecord.appPackageName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            timeString,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            controlRecord.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ControlRecordList() {
    val dateRange = LocalDateRange.current
    val recordList = remember { mutableStateOf(listOf<ControlRecord>()) }

    LaunchedEffect(dateRange.value) {
        withContext(Dispatchers.IO) {
            val (startTimeStamp, endTimeStamp) = dateRange.value
            recordList.value = getControlRecordWithRange(
                startTimeStamp,
                endTimeStamp
            )
        }
    }

    val displayRecordList = recordList.value

    if (displayRecordList.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.control_record_no_record_found),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.offset(0.dp, (-56).dp)
            )
        }
    } else {
        Text (
            stringResource(R.string.control_record_items_found, displayRecordList.size),
            modifier = Modifier.fillMaxWidth().padding(0.dp,0.dp,0.dp,8.dp),
            textAlign = TextAlign.Center
        )
        LazyColumn(
            Modifier
                .padding(24.dp,0.dp)
                .clip(RoundedCornerShape(24.dp)),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(displayRecordList) {
                ControlRecordListItem(it)
            }
        }
    }
}

@Composable
fun ControlRecordContent() {
    ControlRecordDatePicker()
    ControlRecordList()
}

@Composable
fun ControlRecordTopBar() {
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
            text = stringResource(R.string.screen_title_control),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}