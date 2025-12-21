package com.github.xingzheli.antibrainrot.ui.interfaces.settings.control

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_CALM_TIME_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT_LIST
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_ON_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_THRESHOLD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlBypassExpireInterval
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlCalmTime
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMaxTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlThreshold
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlUseTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlBypassExpireInterval
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlCalmTime
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlMaxTimeFactorMultiplier
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlOn
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlThreshold
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlUseTimeFactorMultiplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlEvaluator() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf(String.format(Locale.US, "%.4f", CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT)) }
    var textFieldValue2 by remember { mutableStateOf(String.format(Locale.US, "%.4f", CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT)) }

    val bottomSheetState = LocalBottomSheetState.current
    val bottomSheetContentState     = LocalBottomSheetContentState.current

    LaunchedEffect (Unit) {
        withContext(Dispatchers.IO) {
            textFieldValue = String.format(Locale.US, "%.4f", getControlUseTimeFactorMultiplier())
            textFieldValue2 = String.format(Locale.US, "%.4f", getControlMaxTimeFactorMultiplier())
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text (
                stringResource(R.string.control_item_evaluator_equation),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text (
                stringResource(R.string.control_item_evaluator_equation_explain),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^[+-]?\d*\.?\d*$"""))) {
                        textFieldValue = newValue
                        val newValueFloat = newValue.toFloatOrNull() ?: CONTROL_USE_TIME_FACTOR_MULTIPLIER_DEFAULT
                        coroutineScope.launch {
                            setControlUseTimeFactorMultiplier(newValueFloat)
                        }
                    }
                },
                label = { Text(stringResource(R.string.control_item_use_time_factor_label)) },
                placeholder = { Text(stringResource(R.string.control_item_use_time_factor_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )

            OutlinedTextField(
                value = textFieldValue2,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^[+-]?\d*\.?\d*$"""))) {
                        textFieldValue2 = newValue
                        val newValueFloat = newValue.toFloatOrNull() ?: CONTROL_MAX_TIME_FACTOR_MULTIPLIER_DEFAULT
                        coroutineScope.launch {
                            setControlMaxTimeFactorMultiplier(newValueFloat)
                        }
                    }
                },
                label = { Text(stringResource(R.string.control_item_max_time_factor_label)) },
                placeholder = { Text(stringResource(R.string.control_item_max_time_factor_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )

            TextButton(
                onClick = {
                    showBottomSheet(
                        bottomSheetState,
                        bottomSheetContentState
                    ) {
                        ControlEvaluatorInfo()
                    }
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.padding(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.control_item_what_are_these),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ControlEvaluatorInfo() {
    Column (
        Modifier
            .fillMaxHeight(.85f)
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            stringResource(R.string.control_item_what_are_these),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        val message = stringResource(R.string.control_item_what_are_these_sheet)

        Text(
            message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ControlThreshold() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            textFieldValue = String.format(Locale.US, "%.2f", getControlThreshold())
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.control_threshold_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.control_threshold_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^[+-]?\d*\.?\d*$"""))) {
                        textFieldValue = newValue
                        val newValueFloat = newValue.toFloatOrNull() ?: CONTROL_THRESHOLD_DEFAULT
                        coroutineScope.launch {
                            setControlThreshold(newValueFloat)
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.control_item_threshold_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )
        }
    }
}

@Composable
fun ControlOn() {
    var controlOn by remember { mutableStateOf(CONTROL_ON_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            controlOn = getControlOn()
        }
    }

    ControlItem {
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
                    stringResource(R.string.control_on_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text (
                    stringResource(R.string.control_on_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch (controlOn, onCheckedChange = {
                coroutineScope.launch {
                    setControlOn(!controlOn)
                    controlOn = !controlOn
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlMethod() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(CONTROL_METHOD_DEFAULT) }
    val options = CONTROL_METHOD_DEFAULT_LIST
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            selectedItem = getControlMethod()
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.control_method_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.control_method_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedItem,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                coroutineScope.launch {
                                    setControlMethod(option)
                                    selectedItem = option
                                    expanded = false
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }

    if (selectedItem == "OVERLAY_CALM_DOWN" || selectedItem == "ACTIVITY_CALM_DOWN") {
        ControlCalmTime()
    }
}

@Composable
fun ControlCalmTime() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            textFieldValue = "${getControlCalmTime()}"
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.control_calm_time_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.control_calm_time_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*$"""))) {
                        textFieldValue = newValue
                        val newValueInt = newValue.toIntOrNull() ?: CONTROL_CALM_TIME_DEFAULT
                        coroutineScope.launch {
                            setControlCalmTime(newValueInt)
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.control_item_count_down_time_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )
        }
    }
}

@Composable
fun ControlBypassExpireInterval() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            textFieldValue = "${getControlBypassExpireInterval()}"
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.control_bypass_expire_interval_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.control_bypass_expire_interval_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*$"""))) {
                        textFieldValue = newValue
                        val newValueInt = newValue.toIntOrNull() ?: CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT
                        coroutineScope.launch {
                            setControlBypassExpireInterval(newValueInt)
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.control_item_calm_down_time_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )
        }
    }
}