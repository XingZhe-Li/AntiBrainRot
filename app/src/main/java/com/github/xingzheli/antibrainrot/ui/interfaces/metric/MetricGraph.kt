package com.github.xingzheli.antibrainrot.ui.interfaces.metric

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.xingzheli.antibrainrot.data.room.Metric
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private val BottomAxisValueFormatter =
    object : CartesianValueFormatter {
        private val dateFormat =
            DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")
                .withZone(ZoneId.systemDefault())

        override fun format(
            context: CartesianMeasuringContext,
            value: Double,
            verticalAxisPosition: Axis.Position.Vertical?,
        ) = dateFormat.format(Instant.ofEpochMilli(value.toLong()))
    }

@Composable
fun MetricChart(
    modelProducer: CartesianChartModelProducer,
    vicoColors : List<Color>
) {
    val vicoLines = vicoColors.map {
        LineCartesianLayer.rememberLine(
            fill = LineCartesianLayer.LineFill.single(fill(it)),
            areaFill = LineCartesianLayer.AreaFill.single(
                fill(
                    ShaderProvider.verticalGradient(
                        arrayOf(it.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
            ),
        )
    }

    val vicoLineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(vicoLines)
    )

    val vicoChart = rememberCartesianChart(
        vicoLineLayer,
        startAxis  = VerticalAxis.rememberStart(),
        bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = BottomAxisValueFormatter),
        marker = rememberMarker()
    )

    CartesianChartHost(
        chart = vicoChart,
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modifier = Modifier.fillMaxSize(),
        animationSpec = null
    )
}
@Composable
fun MetricGraph(metricList : List<Metric>) {
    var useTimeFactorMultiplier by LocalUseTimeFactorMultiplier.current
    var maxTimeFactorMultiplier by LocalMaxTimeFactorMultiplier.current
    var threshold by LocalThreshold.current
    var displayOptions by LocalDisplayOptions.current
    var vicoColors by remember { mutableStateOf(listOf<Color>())}

    val numberLimit = 1000
    val filterPossibility =  numberLimit / metricList.size.toFloat()

    val numberLimitedMetricList = if (filterPossibility < 1) {
        metricList.filter {
            Random.nextFloat() <= filterPossibility
        }
    } else {
        metricList
    }

    val timeStamps = numberLimitedMetricList.map {
        it.timeStamp
    }
    val useTimeFactors = numberLimitedMetricList.map {
        it.useTimeFactor
    }
    val maxTimeFactors = numberLimitedMetricList.map {
        it.maxTimeFactor
    }
    val timeInDebts = numberLimitedMetricList.map {
        it.timeInDebt
    }
    val evaluators = numberLimitedMetricList.map {
        it.useTimeFactor * useTimeFactorMultiplier + it.maxTimeFactor * maxTimeFactorMultiplier
    }
    val thresholds = numberLimitedMetricList.map {
        threshold
    }
    val modelProducer = remember { CartesianChartModelProducer() }

    val vicoColorMap = mapOf(
        "useTimeFactor" to MaterialTheme.colorScheme.primary,
        "maxTimeFactor" to MaterialTheme.colorScheme.secondary,
        "timeInDebt"    to MaterialTheme.colorScheme.tertiary,
        "evaluator"     to Color(0xffa485e0),
        "threshold"     to Color(0xFF2483FF)
    )

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp,8.dp)
    ) {
        MetricChart(modelProducer,vicoColors)
    }

    val displayOptionList = listOf (
        "useTimeFactor"    to useTimeFactors,
        "maxTimeFactor"    to maxTimeFactors,
        "timeInDebt"       to timeInDebts,
        "evaluator"        to evaluators ,
        "threshold"        to thresholds
    )

    LaunchedEffect(
        metricList,
        displayOptions,
        useTimeFactorMultiplier,
        maxTimeFactorMultiplier,
        threshold
    ) {
        withContext(Dispatchers.Default) {
            vicoColors = vicoColorMap.filter {
                it.key in displayOptions
            }.map {
                it.value
            }

            modelProducer.runTransaction {
                lineSeries {
                    if (timeStamps.isNotEmpty() && displayOptions.isNotEmpty()) {
                        displayOptionList.forEach {
                            val optionKey = it.first
                            val optionVal = it.second
                            if (optionKey in displayOptions) {
                                series(timeStamps, optionVal)
                            }
                        }
                    } else {
                        vicoColors = listOf(Color.Transparent)
                        series(listOf(0.0), listOf(0.0))
                    }
                }
            }
        }
    }
}