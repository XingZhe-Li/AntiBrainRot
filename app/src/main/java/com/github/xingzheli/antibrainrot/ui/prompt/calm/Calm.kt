package com.github.xingzheli.antibrainrot.ui.prompt.calm


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.github.xingzheli.antibrainrot.core.tracker.setBypass
import com.github.xingzheli.antibrainrot.data.Accessor.insertControlRecord
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceDefaults.CONTROL_CALM_TIME_DEFAULT
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getControlCalmTime
import com.github.xingzheli.antibrainrot.data.room.ControlRecord
import com.github.xingzheli.antibrainrot.ui.prompt.LocalAppName
import com.github.xingzheli.antibrainrot.ui.prompt.LocalAppPackageName
import com.github.xingzheli.antibrainrot.utils.backToHome
import com.github.xingzheli.antibrainrot.utils.finishActivity
import com.github.xingzheli.antibrainrot.utils.startActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Calm() {
    val image = painterResource(R.drawable.icon_dark_full)
    val appName = LocalAppName.current
    val appPackageName = LocalAppPackageName.current
    val coroutineScope = rememberCoroutineScope()
    val activityContext = LocalContext.current

    val text = stringResource(R.string.calm_threshold_exceeded_text)

    var countDownTime by remember { mutableIntStateOf(CONTROL_CALM_TIME_DEFAULT) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            countDownTime = getControlCalmTime()
            while (countDownTime > 0) {
                delay(1000)
                countDownTime -= 1
            }
        }
    }

    Column (
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .height(96.dp)
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

        Text (
            text = text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (countDownTime > 0) {
            Text (
                "$countDownTime",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
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
                                    description = "mode: ACTIVITY_CALM_DOWN , bypass: true"
                                )
                            )
                            setBypass()
                        }
                        finishActivity(activityContext)
                        startActivity(appPackageName)
                    }
                }
            ) {
                Text(stringResource(R.string.general_access_this_time))
            }
        }

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
                                description = "mode: ACTIVITY_CALM_DOWN , bypass: false"
                            )
                        )
                    }
                    finishActivity(activityContext)
                    backToHome()
                }
            }
        ) {
            Text(stringResource(R.string.general_i_m_quitting_button))
        }
    }
}