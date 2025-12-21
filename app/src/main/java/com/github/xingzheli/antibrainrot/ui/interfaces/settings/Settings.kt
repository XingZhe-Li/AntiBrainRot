package com.github.xingzheli.antibrainrot.ui.interfaces.settings

import androidx.compose.runtime.Composable

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import kotlin.math.max
import kotlin.math.min

@Composable
fun SettingsTopbar(scrollState: ScrollState) {
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
            text = stringResource(R.string.screen_title_settings),
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
fun SettingsItem(settingOption : SettingOption) {
    val navHostController = LocalNavHostController.current

    Row (
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable{
                navHostController.navigate(
                    settingOption.route
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            settingOption.icon,
            contentDescription = stringResource(settingOption.tabName),
            modifier = Modifier.fillMaxHeight().padding(16.dp)
        )

        Column (
            Modifier
                .padding(0.dp,16.dp)
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text (
                stringResource(settingOption.tabName),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                stringResource(settingOption.description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun SettingsItemsContainer() {
    Box (
        Modifier
            .fillMaxWidth()
            .padding(24.dp,0.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.spacedBy(2.dp)
        ) {
            for (settingOption in optionList) {
                SettingsItem(settingOption)
            }
        }
    }
}

@Composable
fun SettingsContainer(scrollState: ScrollState) {
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
                text = stringResource(R.string.screen_title_settings),
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
        SettingsItemsContainer()
    }
}

@Composable
fun Settings() {
    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        SettingsTopbar(scrollState)
        SettingsContainer(scrollState)
    }
}