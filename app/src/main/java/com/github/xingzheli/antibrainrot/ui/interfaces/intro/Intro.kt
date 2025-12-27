package com.github.xingzheli.antibrainrot.ui.interfaces.intro

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getConfigTrackMethod
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalIsFirstLoad
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun Intro() {
    val scrollState = rememberScrollState()
    val image = painterResource(R.drawable.icon_dark_full)
    val navHostController = LocalNavHostController.current

    var isFirstLoad by LocalIsFirstLoad.current

    LaunchedEffect(Unit) {
        if (!isFirstLoad) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            val trackMethod = getConfigTrackMethod()
            if (trackMethod == "appUsageEvent") {
                withContext(Dispatchers.Main) {
                    navHostController.navigate(
                        RegistryEntry.LOADING.route
                    )
                    isFirstLoad = false
                }
            }
        }
    }

    Column (
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(24.dp)
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .height(192.dp)
                    .clip(RoundedCornerShape(20))
            )
        }

        Text(
            stringResource(R.string.intro_app_name),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Text(
            stringResource(R.string.intro_app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            Modifier.height(16.dp)
        )

        OptionList()
    }
}

@Composable
fun OptionItem(
    linkOption : LinkOption
) {
    val navHostController = LocalNavHostController.current

    Row (
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable{
                navHostController.navigate(
                    linkOption.route
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            linkOption.icon,
            contentDescription = stringResource(linkOption.tabName),
            modifier = Modifier.fillMaxHeight().padding(16.dp)
        )

        Column (
            Modifier
                .padding(0.dp,16.dp)
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text (
                stringResource(linkOption.tabName),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                stringResource(linkOption.description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OptionList() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp,0.dp)
            .clip(RoundedCornerShape(24.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (linkOption in linkOptionList) {
            OptionItem(linkOption)
        }
    }
}

@Preview
@Composable
fun IntroPreview() {
    Intro()
}