package com.github.xingzheli.antibrainrot.ui.interfaces.settings.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_BYPASS_EXPIRE_INTERVAL_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_METHOD_DEFAULT_LIST
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_DISPLAY_AT_LEAST_LENGTH_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_EXCLUDE_SYSTEM_APPS_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_LANGUAGE_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_LANGUAGE_DEFAULT_LIST
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.UI_USE_DRAWER_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlBypassExpireInterval
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getExcludeSystemApps
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUIUseDrawer
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUiDisplayAtLeastLength
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUiLanguage
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlBypassExpireInterval
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setControlMethod
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setExcludeSystemApps
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setUIUseDrawer
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setUiDisplayAtLeastLength
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.setUiLanguage
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalSnackBarHostState
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.control.ControlCalmTime
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.control.ControlItemAutoHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

@Composable
fun UISettingsTopbar(scrollState: ScrollState) {
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
            text = stringResource(R.string.screen_title_ui_settings),
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
fun UISettingsItem(content : @Composable () -> Unit = {}) {
    Row (
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun UseDrawerOption() {
    var uiUseDrawer by remember { mutableStateOf(UI_USE_DRAWER_DEFAULT) }
    val snackbarHostState = LocalSnackBarHostState.current
    val coroutineScope = rememberCoroutineScope()
    val restartString = stringResource(R.string.restarting_the_app_may_be_required)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            uiUseDrawer = getUIUseDrawer()
        }
    }

    UISettingsItem {
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
                    stringResource(R.string.ui_setting_use_drawer),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text (
                    stringResource(R.string.ui_setting_use_drawer_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch (uiUseDrawer, onCheckedChange = {
                coroutineScope.launch {
                    setUIUseDrawer(it)
                    uiUseDrawer = it
                    launch {
                        snackbarHostState.showSnackbar(restartString)
                    }
                }
            })
        }
    }
}

@Composable
fun ExcludeSystemAppsOption() {
    var excludeSystemApps by remember { mutableStateOf(UI_EXCLUDE_SYSTEM_APPS_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            excludeSystemApps = getExcludeSystemApps()
        }
    }

    UISettingsItem {
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
                    stringResource(R.string.ui_setting_exclude_system_apps),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text (
                    stringResource(R.string.ui_setting_exclude_system_apps_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch (excludeSystemApps, onCheckedChange = {
                coroutineScope.launch {
                    setExcludeSystemApps(!excludeSystemApps)
                    excludeSystemApps = !excludeSystemApps
                }
            })
        }
    }
}

@Composable
fun UISettingsItemsContainer() {
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
            UseDrawerOption()
            ExcludeSystemAppsOption()
            UIDisplayLeastLength()
            UiLanguage()
        }
    }
}

@Composable
fun UISettingsContainer(scrollState: ScrollState) {
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
                text = stringResource(R.string.screen_title_ui_settings),
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
        UISettingsItemsContainer()
    }
}

@Preview
@Composable
fun UISettings() {
    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        UISettingsTopbar(scrollState)
        UISettingsContainer(scrollState)
    }
}


@Composable
fun UIDisplayLeastLength() {
    val coroutineScope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            textFieldValue = "${getUiDisplayAtLeastLength()}"
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.ui_setting_display_least_length),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.ui_setting_display_least_length_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*$"""))) {
                        textFieldValue = newValue
                        val newValueLong = newValue.toLongOrNull() ?: UI_DISPLAY_AT_LEAST_LENGTH_DEFAULT
                        coroutineScope.launch {
                            setUiDisplayAtLeastLength(newValueLong)
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.ui_setting_default_value_ms)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiLanguage() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(UI_LANGUAGE_DEFAULT) }
    val options = UI_LANGUAGE_DEFAULT_LIST
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackBarHostState.current
    val restartString = stringResource(R.string.restarting_the_app_may_be_required)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            selectedItem = getUiLanguage()
        }
    }

    ControlItemAutoHeight {
        Column (
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.settings_ui_language),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                stringResource(R.string.settings_ui_change_display_language),
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
                                    withContext(Dispatchers.IO) {
                                        setUiLanguage(option)
                                        selectedItem = option
                                        expanded = false

                                        snackbarHostState.showSnackbar(restartString)
                                    }
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}