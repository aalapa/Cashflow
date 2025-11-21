package com.cashflow.app.ui.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashflow.app.domain.model.CashFlowDay

@Composable
fun CashFlowChart(
    cashFlowDays: List<CashFlowDay>,
    modifier: Modifier = Modifier,
    showBarChart: Boolean = true
) {
    if (cashFlowDays.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data to display",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Y-axis labels on the left
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Y-axis labels column
                Column(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val maxBalance = cashFlowDays.maxOfOrNull { it.balance } ?: 1000.0
                    val minBalance = cashFlowDays.minOfOrNull { it.balance } ?: 0.0
                    
                    // Top label (max)
                    Text(
                        text = formatCurrencyCompact(maxBalance),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                    
                    // Middle label (zero or mid-point)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formatCurrencyCompact(0.0),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Bottom label (min)
                    Text(
                        text = formatCurrencyCompact(minBalance),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
                
                // Chart area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (showBarChart) {
                        BarChart(cashFlowDays = cashFlowDays)
                    } else {
                        LineChart(cashFlowDays = cashFlowDays)
                    }
                }
            }
            
            // X-axis labels at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(start = 60.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Show first, middle, and last dates
                val firstDate = cashFlowDays.firstOrNull()?.date
                val lastDate = cashFlowDays.lastOrNull()?.date
                val middleIndex = cashFlowDays.size / 2
                val middleDate = cashFlowDays.getOrNull(middleIndex)?.date
                
                if (firstDate != null) {
                    Text(
                        text = formatDateShort(firstDate),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (middleDate != null && cashFlowDays.size > 2) {
                    Text(
                        text = formatDateShort(middleDate),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                
                if (lastDate != null) {
                    Text(
                        text = formatDateShort(lastDate),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BarChart(cashFlowDays: List<CashFlowDay>) {
    val maxBalance = cashFlowDays.maxOfOrNull { it.balance } ?: 1000.0
    val minBalance = cashFlowDays.minOfOrNull { it.balance } ?: 0.0
    val range = (maxBalance - minBalance).coerceAtLeast(100.0)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = (size.width / cashFlowDays.size.coerceAtLeast(1)).coerceAtLeast(1f)
        val chartHeight = size.height * 0.85f
        val paddingTop = size.height * 0.05f
        
        // Calculate zero line position
        val zeroY = if (minBalance >= 0) {
            // All positive: zero at bottom
            paddingTop + chartHeight
        } else if (maxBalance <= 0) {
            // All negative: zero at top
            paddingTop
        } else {
            // Mixed: proportional position
            paddingTop + chartHeight * (maxBalance / range).toFloat()
        }

        // Draw zero line
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(0f, zeroY),
            end = Offset(size.width, zeroY),
            strokeWidth = 1.5f
        )

        // Draw Y-axis grid lines
        val gridSteps = 4
        for (i in 0..gridSteps) {
            val y = paddingTop + (i.toFloat() / gridSteps) * chartHeight
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 0.5f
            )
        }

        // Draw bars
        cashFlowDays.forEachIndexed { index, day ->
            val x = index * barWidth + barWidth / 2
            
            val color = when {
                day.isNegative -> Color(0xFFEF4444)
                day.isWarning -> Color(0xFFFFB020)
                else -> Color(0xFF10B981)
            }

            // Calculate bar padding based on bar width (adaptive padding)
            val barPadding = if (barWidth > 4.dp.toPx()) {
                2.dp.toPx()
            } else {
                (barWidth * 0.1f).coerceAtLeast(0.5f)
            }
            val effectiveBarWidth = (barWidth - 2 * barPadding).coerceAtLeast(1f)

            // Calculate bar height as pixels from zero line
            val barHeightInPixels = if (range > 0) {
                (kotlin.math.abs(day.balance) / range * chartHeight).toFloat()
            } else {
                0f
            }

            if (day.balance >= 0) {
                // Positive balance: bar extends upward from zero line
                val barY = (zeroY - barHeightInPixels).coerceAtLeast(paddingTop)
                val barHeight = (zeroY - barY).coerceAtLeast(1f)
                drawRect(
                    color = color,
                    topLeft = Offset(x - effectiveBarWidth / 2, barY),
                    size = Size(effectiveBarWidth, barHeight)
                )
            } else {
                // Negative balance: bar extends downward from zero line
                val barHeight = barHeightInPixels.coerceAtMost(chartHeight - (zeroY - paddingTop)).coerceAtLeast(1f)
                drawRect(
                    color = color,
                    topLeft = Offset(x - effectiveBarWidth / 2, zeroY),
                    size = Size(effectiveBarWidth, barHeight)
                )
            }
        }
    }
}

@Composable
fun LineChart(cashFlowDays: List<CashFlowDay>) {
    val maxBalance = cashFlowDays.maxOfOrNull { it.balance } ?: 1000.0
    val minBalance = cashFlowDays.minOfOrNull { it.balance } ?: 0.0
    val range = (maxBalance - minBalance).coerceAtLeast(100.0)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val chartWidth = size.width * 0.98f
        val chartHeight = size.height * 0.85f
        val paddingX = size.width * 0.01f
        val paddingTop = size.height * 0.05f
        
        // Calculate zero line position
        val zeroY = if (minBalance >= 0) {
            // All positive: zero at bottom
            paddingTop + chartHeight
        } else if (maxBalance <= 0) {
            // All negative: zero at top
            paddingTop
        } else {
            // Mixed: proportional position
            paddingTop + chartHeight * (maxBalance / range).toFloat()
        }

        // Draw zero line
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(paddingX, zeroY),
            end = Offset(paddingX + chartWidth, zeroY),
            strokeWidth = 1.5f
        )

        // Draw Y-axis grid lines
        val gridSteps = 4
        for (i in 0..gridSteps) {
            val y = paddingTop + (i.toFloat() / gridSteps) * chartHeight
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(paddingX, y),
                end = Offset(paddingX + chartWidth, y),
                strokeWidth = 0.5f
            )
        }

        if (cashFlowDays.isNotEmpty()) {
            val path = Path()
            val pointSize = 4.dp.toPx()

            cashFlowDays.forEachIndexed { index, day ->
                val x = paddingX + (index.toFloat() / (cashFlowDays.size - 1).coerceAtLeast(1)) * chartWidth
                
                // Calculate y position from zero line
                val yOffset = if (range > 0) {
                    (day.balance / range * chartHeight).toFloat()
                } else {
                    0f
                }
                val y = zeroY - yOffset
                val point = Offset(x, y.coerceIn(paddingTop, paddingTop + chartHeight))

                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }

                // Draw point
                val color = when {
                    day.isNegative -> Color(0xFFEF4444)
                    day.isWarning -> Color(0xFFFFB020)
                    else -> Color(0xFF10B981)
                }
                drawCircle(
                    color = color,
                    radius = pointSize,
                    center = point
                )
            }

            // Draw line
            drawPath(
                path = path,
                color = Color(0xFF2563EB),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

fun formatDateShort(date: kotlinx.datetime.LocalDate): String {
    return "${date.monthNumber}/${date.dayOfMonth}"
}

