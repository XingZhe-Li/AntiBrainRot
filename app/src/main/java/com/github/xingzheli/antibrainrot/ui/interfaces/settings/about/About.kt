package com.github.xingzheli.antibrainrot.ui.interfaces.settings.about

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.ui.interfaces.settings.control.showBottomSheet
import kotlinx.coroutines.launch

import kotlin.math.max
import kotlin.math.min

@Composable
fun AboutTopbar(scrollState: ScrollState) {
    val titleAlpha = min(
        max(0.0f,(scrollState.value - 140) / 240.0f),
        1.0f
    )
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
            text = stringResource(R.string.screen_title_about),
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
fun AboutContainer(scrollState: ScrollState,modifier : Modifier) {
    val titleAlpha = min(
        max(0.0f,(180 - scrollState.value) / 100.0f),
        1.0f
    )

    Column (
        modifier
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
                text = stringResource(R.string.screen_title_about),
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
        AboutItemsContainer()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalBottomSheetState = compositionLocalOf<MutableState<Boolean>> {
    error("BottomSheetState not provided")
}

val LocalBottomSheetContentState = compositionLocalOf<MutableState<@Composable ()->Unit>> {
    error("BottomSheetContent not provided")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About() {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState  = remember { mutableStateOf(false) }
    val bottomSheetContentState = remember { mutableStateOf(@Composable {}) }

    CompositionLocalProvider(
        LocalBottomSheetState provides bottomSheetState,
        LocalBottomSheetContentState provides bottomSheetContentState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            AboutTopbar(scrollState)
            AboutContainer(scrollState, Modifier.weight(1f))
            AboutVersion()
        }
    }

    if (bottomSheetState.value) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { bottomSheetState.value = false }
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            bottomSheetContentState.value()
        }
    }
}

@Composable
fun AboutVersion() {
    Text(
        stringResource(R.string.about_version_info),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(0.dp,8.dp)
    )
}

@Composable
fun LicenseInfo() {
    val scrollState = rememberScrollState()

    Column (
        Modifier.verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.about_license_info),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AboutItemsContainer() {
    val bottomSheetState = LocalBottomSheetState.current
    val bottomSheetContentState     = LocalBottomSheetContentState.current

    Column (
        Modifier
            .fillMaxWidth()
            .padding(24.dp,0.dp)
    ) {
        Text(stringResource(R.string.about_app_description))

        TextButton(
            onClick = {
                showBottomSheet(
                    bottomSheetState,
                    bottomSheetContentState
                ) {
                    LicenseInfo()
                }
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.about_open_source_licenses),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(stringResource(R.string.about_author_info))
    }
}