package com.github.xingzheli.antibrainrot.ui.interfaces.metric

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_THRESHOLD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMaxTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlThreshold
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlUseTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlMaxTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlThreshold
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlUseTimeFactorMultiplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

val LocalDisplayOptions = compositionLocalOf<MutableState<List<String>>> {
    error("displayOptions not provided")
}

@Composable
fun DisplayOptions() {
    val displayOptions = LocalDisplayOptions.current
    val displayOptionList = listOf (
        "Use Time Factor" to "useTimeFactor" ,
        "Max Time Factor" to "maxTimeFactor" ,
        "Time In Debt"    to "timeInDebt" ,
        "Evaluator"       to "evaluator"  ,
        "Threshold"       to "threshold"
    )
    val scrollState = rememberScrollState()

    val vicoColorMap = mapOf(
        "useTimeFactor" to MaterialTheme.colorScheme.primary,
        "maxTimeFactor" to MaterialTheme.colorScheme.secondary,
        "timeInDebt"    to MaterialTheme.colorScheme.tertiary,
        "evaluator"     to Color(0xffa485e0),
        "threshold"     to Color(0xFF2483FF)
    )

    Column (
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            stringResource(R.string.metric_display_options),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.padding(0.dp,0.dp,0.dp,8.dp)
        )

        Row (
            Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            for (displayOption in displayOptionList) {
                val displayName = displayOption.first
                val displayKey  = displayOption.second
                val enabled = displayKey in displayOptions.value
                FilterChip(
                    selected = enabled,
                    onClick = {
                        if (enabled) {
                            displayOptions.value = displayOptions.value.filter { it != displayKey }
                        } else {
                            displayOptions.value = displayOptions.value + listOf(displayKey)
                        }
                    },
                    label = { Text(displayName) },
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = 4.dp),
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = vicoColorMap[displayKey]!!,
                        selectedLabelColor = MaterialTheme.colorScheme.inverseOnSurface
                    )
                )
            }
        }
    }
}
val LocalUseTimeFactorMultiplier = compositionLocalOf<MutableFloatState> {
    error("UseTimeFactorMultiplier not provided")
}

val LocalMaxTimeFactorMultiplier = compositionLocalOf<MutableFloatState> {
    error("MaxTimeFactorMultiplier not provided")
}

val LocalThreshold = compositionLocalOf<MutableFloatState> {
    error("threshold not provided")
}

@Composable
fun EvaluatorOptionItem(
    label : String,
    placeholder : String,
    defaultVal  : Float,
    coroutineScope: CoroutineScope,
    state: MutableFloatState,
    textField: MutableState<String>,
    setFunc: suspend (Float)->Unit,
    getFunc: suspend ()->Float
) {
    Row (
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textField.value,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("""^[+-]?\d*\.?\d*$"""))) {
                    val newValueFloat = newValue.toFloatOrNull() ?: defaultVal
                    state.floatValue = newValueFloat
                    textField.value = newValue
                }
            },
            modifier = Modifier.weight(1f)
        )

        IconButton (
            onClick = {
                coroutineScope.launch {
                    setFunc(state.floatValue)
                }
            }
        ) {
            Icon(
                Icons.Default.Save,
                contentDescription = stringResource(R.string.metric_options_save)
            )
        }

        IconButton (
            onClick = {
                coroutineScope.launch {
                    val fetchVal = getFunc()
                    state.floatValue = fetchVal
                    textField.value = String.format(Locale.US, "%.4f", fetchVal)
                }
            }
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = stringResource(R.string.metric_options_refresh)
            )
        }
    }
}

@Composable
fun EvaluatorOptions() {
    val useTimeFactorMultiplier = LocalUseTimeFactorMultiplier.current
    val maxTimeFactorMultiplier = LocalMaxTimeFactorMultiplier.current
    val threshold = LocalThreshold.current
    val coroutineScope = rememberCoroutineScope()

    val useTimeTextField = remember { mutableStateOf("") }
    val maxTimeTextField = remember { mutableStateOf("") }
    val thresholdTextField = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val useTimeFactorFetch = getControlUseTimeFactorMultiplier()
            useTimeFactorMultiplier.floatValue = useTimeFactorFetch
            val maxTimeFactorFetch = getControlMaxTimeFactorMultiplier()
            maxTimeFactorMultiplier.floatValue = maxTimeFactorFetch
            val thresholdFetch = getControlThreshold()
            threshold.floatValue = thresholdFetch

            useTimeTextField.value = String.format(Locale.US, "%.4f", useTimeFactorFetch)
            maxTimeTextField.value = String.format(Locale.US, "%.4f", maxTimeFactorFetch)
            thresholdTextField.value = String.format(Locale.US, "%.4f", thresholdFetch)
        }
    }

    Column (
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(R.string.metric_evaluator_options),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        EvaluatorOptionItem(
            label = stringResource(R.string.control_item_use_time_factor_label),
            placeholder = stringResource(R.string.metric_evaluator_options_use_time_placeholder),
            defaultVal = CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT,
            coroutineScope = coroutineScope,
            state = useTimeFactorMultiplier,
            textField = useTimeTextField,
            setFunc = {
                setControlUseTimeFactorMultiplier(it)
            },
            getFunc = {
                getControlUseTimeFactorMultiplier()
            }
        )

        EvaluatorOptionItem(
            label = stringResource(R.string.control_item_max_time_factor_label),
            placeholder = stringResource(R.string.metric_evaluator_options_max_time_placeholder),
            defaultVal = CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT,
            coroutineScope = coroutineScope,
            state = maxTimeFactorMultiplier,
            textField = maxTimeTextField,
            setFunc = {
                setControlMaxTimeFactorMultiplier(it)
            },
            getFunc = {
                getControlMaxTimeFactorMultiplier()
            }
        )

        EvaluatorOptionItem(
            label = stringResource(R.string.metric_evaluator_options_threshold_label),
            placeholder = stringResource(R.string.metric_evaluator_options_threshold_placeholder),
            defaultVal = CONTROL_THRESHOLD_DEFAULT,
            coroutineScope = coroutineScope,
            state = threshold,
            textField = thresholdTextField,
            setFunc = {
                setControlThreshold(it)
            },
            getFunc = {
                getControlThreshold()
            }
        )
    }
}

@Composable
fun MetricOptions() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp,0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DisplayOptions()
        EvaluatorOptions()
    }
}