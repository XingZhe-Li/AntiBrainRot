package com.github.xingzheli.antibrainrot.ui.prompt.block

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.R
import com.github.xingzheli.antibrainrot.data.Accessor.insertControlRecord
import com.github.xingzheli.antibrainrot.data.room.ControlRecord
import com.github.xingzheli.antibrainrot.ui.prompt.LocalAppName
import com.github.xingzheli.antibrainrot.ui.prompt.LocalAppPackageName
import com.github.xingzheli.antibrainrot.utils.backToHome
import com.github.xingzheli.antibrainrot.utils.finishActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Block() {
    val image = painterResource(R.drawable.icon_dark_full)
    val coroutineScope = rememberCoroutineScope()
    val activityContext = LocalContext.current
    val appPackageName = LocalAppPackageName.current
    val appName = LocalAppName.current


    val text = stringResource(R.string.block_threshold_exceeded_text)

    Column (
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Button(
            {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val timeStamp = System.currentTimeMillis()
                        insertControlRecord(
                            ControlRecord(
                                timeStamp = timeStamp,
                                appPackageName = appPackageName,
                                appName = appName,
                                description = "mode: ACTIVITY_BLOCK"
                            )
                        )
                    }
                    finishActivity(activityContext)
                    backToHome()
                }
            }
        ) {
            Text(stringResource(R.string.general_i_understand_button))
        }
    }
}