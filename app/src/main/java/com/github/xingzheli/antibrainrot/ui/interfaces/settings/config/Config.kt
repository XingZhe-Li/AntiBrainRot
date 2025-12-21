package com.github.xingzheli.antibrainrot.ui.interfaces.settings.config

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.Accessor.insertMetricRecord
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_FAST_START_HOURS_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_MAX_LOSS_RATE_PER_DAY_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_TRACK_ON_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONFIG_USAGE_LOSS_RATE_PER_DAY_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getFastStartHours
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getMaxLossRatePerDay
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getTrackOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUsageLossRatePerDay
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setFastStartHours
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setMaxLossRatePerDay
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setTrackOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setUsageLossRatePerDay
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalSnackBarHostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun ConfigTopbar(scrollState: ScrollState) {
    val titleAlpha = min(
        max(0.0f,(scrollState.value - 140) / 240.0f),
        1.0f
    )
    val navHostController = LocalNavHostController.current

    Row (
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp, 0.dp),
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
            text = stringResource(R.string.screen_title_configuration),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .alpha(titleAlpha),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ConfigItem(height : Dp = 72.dp,content : @Composable () -> Unit = {}) {
    Row (
        Modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun ConfigItemAutoHeight(content : @Composable () -> Unit = {}) {
    Row (
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun ConfigItemClickable(content : @Composable () -> Unit,onClick : ()->Unit) {
    Row (
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ConfigMaxLossPerDay() {
    var sliderPosition by remember { mutableFloatStateOf(CONFIG_MAX_LOSS_RATE_PER_DAY_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            sliderPosition = getMaxLossRatePerDay()
        }
    }

    ConfigItemAutoHeight {
        Box (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column (
                Modifier.fillMaxSize()
            ) {
                Row (
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.config_max_loss_per_day_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(String.format("%.2f",sliderPosition))
                }

                Text(
                    stringResource(R.string.config_max_loss_per_day_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        coroutineScope.launch {
                            setMaxLossRatePerDay(newValue)
                        }
                        sliderPosition = newValue
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ConfigUsageLossPerDay() {
    var sliderPosition by remember { mutableFloatStateOf(CONFIG_USAGE_LOSS_RATE_PER_DAY_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            sliderPosition = getUsageLossRatePerDay()
        }
    }

    ConfigItemAutoHeight {
        Box (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column (
                Modifier.fillMaxSize()
            ) {
                Row (
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.config_usage_loss_per_day_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(String.format("%.2f",sliderPosition))
                }

                Text(
                    stringResource(R.string.config_usage_loss_per_day_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        coroutineScope.launch {
                            setUsageLossRatePerDay(newValue)
                        }
                        sliderPosition = newValue
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ConfigFastStartHours() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val fastStartHours = getFastStartHours()
            textFieldValue = String.format(Locale.US, "%.2f", fastStartHours)
        }
    }

    ConfigItemAutoHeight {
        Box (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column (
                Modifier.fillMaxSize()
            ) {
                Text(
                    stringResource(R.string.config_fast_start_hours_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    stringResource(R.string.config_fast_start_hours_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                            textFieldValue = newValue
                            val newValueFloat = newValue.toFloatOrNull() ?: CONFIG_FAST_START_HOURS_DEFAULT
                            coroutineScope.launch {
                                setFastStartHours(newValueFloat)
                            }
                        }
                    },
                    placeholder = { Text(stringResource(R.string.config_fast_start_hours_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(0.dp,8.dp),
                )
            }
        }
    }
}

@Composable
fun ConfigTrackOn() {
    var configTrackOn by remember { mutableStateOf(CONFIG_TRACK_ON_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            configTrackOn = getTrackOn()
        }
    }

    ConfigItem {
        Row (
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text (
                    stringResource(R.string.config_track_on_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text (
                    stringResource(R.string.config_track_on_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch (configTrackOn, onCheckedChange = {
                coroutineScope.launch {
                    setTrackOn(!configTrackOn)
                    configTrackOn = !configTrackOn
                }
            })
        }
    }
}

@Composable
fun ConfigItemsContainer() {
    Box (
        Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.spacedBy(2.dp)
        ) {
            ConfigTrackOn()
            ConfigUsageLossPerDay()
            ConfigMaxLossPerDay()
            ConfigFastStartHours()
            ConfigStatusReset()
        }
    }
}

@Composable
fun ConfigContainer(scrollState: ScrollState) {
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
                .padding(24.dp, 0.dp)
                .height(100.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = stringResource(R.string.screen_title_configuration),
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
        ConfigItemsContainer()
    }
}

@Composable
fun ConfigStatusReset() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackBarHostState.current

    val stringResource = stringResource(R.string.config_status_reset_snackbar)

    ConfigItem {
        Row (
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text (
                    stringResource(R.string.config_status_reset_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text (
                    stringResource(R.string.config_status_reset_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val timestamp = System.currentTimeMillis()
                        insertMetricRecord(
                            Metric(
                                timeStamp = timestamp,
                                totalUsedTime = 0,
                                useTimeFactor = 0.0,
                                timeInDebt = 0.0,
                                maxTimeFactor = 0.0
                            )
                        )
                        launch {
                            snackbarHostState.showSnackbar(stringResource)
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.config_status_reset_button_description)
                )
            }
        }
    }
}

@Composable
fun Config() {
    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        ConfigTopbar(scrollState)
        ConfigContainer(scrollState)
    }
}