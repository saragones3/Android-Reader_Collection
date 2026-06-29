/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 29/6/2026
 */

package aragones.sergio.readercollection.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.presentation.statistics.Entries
import aragones.sergio.readercollection.presentation.statistics.Entry
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin

@Composable
fun BarChart(entries: Entries, onEntrySelected: (Int?) -> Unit) {
    val barEntries = entries.entries.sortedBy { it.key }
    val colorPrimary = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()
    val valueTextStyle = MaterialTheme.typography.labelSmall.copy(color = colorPrimary)
    val axisTextStyle = MaterialTheme.typography.labelMedium.copy(color = colorPrimary)

    // Animate bar height from 0→1
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
    }

    val scrollState = rememberScrollState(initial = Int.MAX_VALUE)
    val itemWidthDp = 45.dp

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        BoxWithConstraints {
            val calculatedWidth = itemWidthDp * barEntries.size + 32.dp
            val finalWidth = if (calculatedWidth > maxWidth) calculatedWidth else maxWidth

            Box(modifier = Modifier.horizontalScroll(scrollState)) {
                Canvas(
                    modifier = Modifier
                        .height(250.dp)
                        .width(finalWidth)
                        .pointerInput(barEntries) {
                            detectTapGestures { offset ->
                                val paddingLeft = 16.dp.toPx()
                                val paddingRight = 16.dp.toPx()
                                val chartWidth = size.width - paddingLeft - paddingRight
                                val itemWidth = chartWidth / barEntries.size
                                val relX = offset.x - paddingLeft
                                val index = (relX / itemWidth).toInt().coerceIn(
                                    0,
                                    barEntries.size - 1,
                                )
                                onEntrySelected(barEntries[index].key.toIntOrNull())
                            }
                        },
                ) {
                    val paddingLeft = 16.dp.toPx()
                    val paddingRight = 16.dp.toPx()
                    val paddingBottom = 28.dp.toPx()
                    val paddingTop = 24.dp.toPx()
                    val chartWidth = size.width - paddingLeft - paddingRight
                    val chartHeight = size.height - paddingBottom - paddingTop
                    val maxVal = barEntries.maxOfOrNull { it.size }?.toFloat() ?: 1f
                    val itemWidth = chartWidth / barEntries.size
                    val barSpacing = itemWidth * 0.25f
                    val actualBarWidth = itemWidth - barSpacing

                    // Draw baseline
                    drawLine(
                        color = colorPrimary.copy(alpha = 0.3f),
                        start = Offset(paddingLeft, size.height - paddingBottom),
                        end = Offset(size.width - paddingRight, size.height - paddingBottom),
                        strokeWidth = 1.dp.toPx(),
                    )

                    barEntries.forEachIndexed { index, entry ->
                        val fullBarHeight = (entry.size / maxVal) * chartHeight
                        val animatedBarHeight = fullBarHeight * animProgress.value
                        val x = paddingLeft + index * itemWidth + barSpacing / 2
                        val y = size.height - paddingBottom - animatedBarHeight

                        drawRoundRect(
                            color = colorPrimary,
                            topLeft = Offset(x, y),
                            size = Size(actualBarWidth, animatedBarHeight),
                            cornerRadius = CornerRadius(3.dp.toPx()),
                        )

                        // Value label above bar
                        if (animProgress.value > 0.8f) {
                            val valueText = entry.size.toString()
                            val valueMeasured = textMeasurer.measure(valueText, valueTextStyle)
                            drawText(
                                textLayoutResult = valueMeasured,
                                topLeft = Offset(
                                    x + (actualBarWidth - valueMeasured.size.width) / 2,
                                    y - valueMeasured.size.height - 4.dp.toPx(),
                                ),
                            )
                        }

                        // X-axis label (year)
                        val keyText = entry.key
                        val keyMeasured = textMeasurer.measure(keyText, axisTextStyle)
                        drawText(
                            textLayoutResult = keyMeasured,
                            topLeft = Offset(
                                x + (actualBarWidth - keyMeasured.size.width) / 2,
                                size.height - paddingBottom + 6.dp.toPx(),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalBarChart(entries: Entries, onEntrySelected: (String) -> Unit) {
    val colorPrimary = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()
    val labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = colorPrimary)
    val valueTextStyle = MaterialTheme.typography.labelSmall.copy(color = colorPrimary)
    val chartHeight = (entries.entries.size * 50).dp.coerceAtLeast(200.dp)

    // Animate bar width from 0→1
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Canvas(
            modifier = Modifier
                .height(chartHeight)
                .fillMaxWidth()
                .pointerInput(entries) {
                    detectTapGestures { offset ->
                        val barHeight = size.height.toFloat() / entries.entries.size
                        val index = (offset.y / barHeight)
                            .toInt()
                            .coerceIn(0, entries.entries.size - 1)
                        onEntrySelected(entries.entries.reversed()[index].key)
                    }
                },
        ) {
            val maxVal = entries.entries.maxOfOrNull { it.size }?.toFloat() ?: 1f
            val labelAreaWidth = 100.dp.toPx()
            val rightPadding = 48.dp.toPx()
            val chartWidth = size.width - labelAreaWidth - rightPadding
            val barHeight = size.height / entries.entries.size
            val barSpacing = barHeight * 0.3f
            val actualBarHeight = barHeight - barSpacing

            // Draw vertical axis line
            drawLine(
                color = colorPrimary.copy(alpha = 0.3f),
                start = Offset(labelAreaWidth, 0f),
                end = Offset(labelAreaWidth, size.height),
                strokeWidth = 1.dp.toPx(),
            )

            entries.entries.reversed().forEachIndexed { index, entry ->
                val normalizedWidth = (entry.size / maxVal) * chartWidth
                val animatedWidth = normalizedWidth * animProgress.value
                val x = labelAreaWidth
                val y = index * barHeight + barSpacing / 2

                drawRoundRect(
                    color = colorPrimary,
                    topLeft = Offset(x, y),
                    size = Size(animatedWidth, actualBarHeight),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                )

                // Y-axis label (left of axis)
                val keyText = entry.key.take(14) // truncate long names
                val keyMeasured = textMeasurer.measure(keyText, labelTextStyle)
                drawText(
                    textLayoutResult = keyMeasured,
                    topLeft = Offset(
                        (labelAreaWidth - keyMeasured.size.width - 8.dp.toPx())
                            .coerceAtLeast(0f),
                        y + (actualBarHeight - keyMeasured.size.height) / 2,
                    ),
                )

                // Value label (right of bar)
                if (animProgress.value > 0.8f) {
                    val valueText = entry.size.toString()
                    val valueMeasured = textMeasurer.measure(valueText, valueTextStyle)
                    drawText(
                        textLayoutResult = valueMeasured,
                        topLeft = Offset(
                            x + animatedWidth + 6.dp.toPx(),
                            y + (actualBarHeight - valueMeasured.size.height) / 2,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(
    entries: Entries,
    centerText: String,
    usePercent: Boolean,
    onEntrySelected: (String) -> Unit,
) {
    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSecondary = MaterialTheme.colorScheme.secondary
    val textMeasurer = rememberTextMeasurer()
    val centerTextStyle = MaterialTheme.typography.labelLarge.copy(
        color = colorPrimary,
        textAlign = TextAlign.Center,
    )
    val labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = colorPrimary)
    val valueTextStyle = MaterialTheme.typography.labelSmall.copy(
        color = colorSecondary.copy(alpha = 0.85f),
    )

    val total = entries.entries.sumOf { it.size }.toFloat()

    // Animate sweep from 0→360
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(1400, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(entries) {
                    detectTapGestures { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val rawAngle = (
                            atan2(
                                offset.y - center.y,
                                offset.x - center.x,
                            ) * 180.0 / PI
                            ).toFloat()
                        // Convert from atan2 system (0=right, CCW) to MPAndroidChart system
                        // (start=-90 degrees = top)
                        val angle = ((rawAngle + 90f) + 360f) % 360f

                        var currentAngle = 0f
                        entries.entries.forEach { entry ->
                            val sweepAngle = (entry.size / total) * 360f
                            if (angle >= currentAngle && angle <= currentAngle + sweepAngle) {
                                onEntrySelected(entry.key)
                                return@detectTapGestures
                            }
                            currentAngle += sweepAngle
                        }
                    }
                },
        ) {
            drawMPPieChart(
                entries = entries.entries,
                total = total,
                animProgress = animProgress.value,
                colorPrimary = colorPrimary,
                usePercent = usePercent,
                textMeasurer = textMeasurer,
                labelTextStyle = labelTextStyle,
                valueTextStyle = valueTextStyle,
            )
        }
        // Center text (like MPAndroidChart centerText)
        Text(
            text = centerText,
            style = centerTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

private fun DrawScope.drawMPPieChart(
    entries: List<Entry>,
    total: Float,
    animProgress: Float,
    colorPrimary: Color,
    usePercent: Boolean,
    textMeasurer: TextMeasurer,
    labelTextStyle: TextStyle,
    valueTextStyle: TextStyle,
) {
    val strokeWidth = 40.dp.toPx()
    val radius = (size.minDimension * 0.28f)
    val center = Offset(size.width / 2f, size.height / 2f)
    val sliceSpace = 1.5f // degrees gap between slices

    var startAngle = -90f
    val totalAnimated = animProgress * 360f

    entries.forEach { entry ->
        val sweepAngle = (entry.size / total) * 360f
        val animatedSweep = (sweepAngle * animProgress).coerceAtMost(
            totalAnimated - (startAngle + 90f).coerceAtLeast(0f),
        )

        // Shift highlighted slice outward
        val midAngle = startAngle + sweepAngle / 2f

        if (animatedSweep > 0f) {
            drawArc(
                color = colorPrimary,
                startAngle = startAngle + sliceSpace / 2,
                sweepAngle = (animatedSweep - sliceSpace).coerceAtLeast(0f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius,
                ),
            )
        }

        // Only draw outside labels after animation completes (animProgress > 0.85)
        if (animProgress > 0.85f && sweepAngle > 1f) {
            val part1End = radius + strokeWidth / 2 + 4.dp.toPx()

            val midAngleRad = midAngle * PI / 180
            val cosA = cos(midAngleRad).toFloat()
            val sinA = sin(midAngleRad).toFloat()

            // Connector line part 1 (from slice edge outward)
            val lineStart = Offset(
                center.x + (radius + strokeWidth / 2) * cosA,
                center.y + (radius + strokeWidth / 2) * sinA,
            )
            val lineKink = Offset(
                center.x + part1End * cosA,
                center.y + part1End * sinA,
            )
            drawLine(
                color = colorPrimary,
                start = lineStart,
                end = lineKink,
                strokeWidth = 1.5.dp.toPx(),
            )

            // Connector line part 2 (horizontal)
            val isRight = cosA >= 0
            val horizEnd = Offset(
                lineKink.x + (if (isRight) 1f else -1f) * 18.dp.toPx(),
                lineKink.y,
            )
            drawLine(
                color = colorPrimary,
                start = lineKink,
                end = horizEnd,
                strokeWidth = 1.5.dp.toPx(),
            )

            // Entry label (key)
            val labelText = entry.key
            val labelMeasured = textMeasurer.measure(labelText, labelTextStyle)
            val labelX = if (isRight) {
                horizEnd.x + 4.dp.toPx()
            } else {
                horizEnd.x - labelMeasured.size.width - 4.dp.toPx()
            }
            drawText(
                textLayoutResult = labelMeasured,
                topLeft = Offset(
                    labelX,
                    lineKink.y - labelMeasured.size.height / 2,
                ),
            )

            // Value text (count or percent) inside the slice
            val valueText = if (usePercent) {
                val pct = (entry.size / total * 100.0)
                "${pct.format(1)}%"
            } else {
                entry.size.toString()
            }
            val valueMeasured = textMeasurer.measure(valueText, valueTextStyle)
            drawText(
                textLayoutResult = valueMeasured,
                topLeft = Offset(
                    center.x + radius * cosA - valueMeasured.size.width / 2,
                    center.y + radius * sinA - valueMeasured.size.height / 2,
                ),
            )
        }

        startAngle += sweepAngle
    }
}

private fun Double.format(digits: Int): String {
    val multiplier = 10.0.pow(digits)
    val totalUnits = round(this * multiplier).toLong()
    val integerPart = totalUnits / multiplier.toLong()
    val fractionalPart = totalUnits % multiplier.toLong()
    return if (digits > 0) {
        "$integerPart.${fractionalPart.toString().padStart(digits, '0')}"
    } else {
        integerPart.toString()
    }
}