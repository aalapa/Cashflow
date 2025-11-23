package com.cashflow.app.ui.envelopes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.domain.model.Envelope
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.repository.MonthlySpending
import com.cashflow.app.ui.timeline.formatCurrency
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeAnalyticsScreen(
    repository: CashFlowRepository,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: EnvelopeAnalyticsViewModel = viewModel { EnvelopeAnalyticsViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Envelope Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.envelopes) { envelope ->
                        val trend = state.spendingTrends[envelope.id] ?: emptyList()
                        val totalSpent = state.spendingByEnvelope[envelope.id] ?: 0.0
                        
                        AnalyticsCard(
                            envelope = envelope,
                            trend = trend,
                            totalSpent = totalSpent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    envelope: Envelope,
    trend: List<MonthlySpending>,
    totalSpent: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = getIconForString(envelope.icon ?: "Folder"),
                    contentDescription = null,
                    tint = envelope.color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = envelope.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Spent: ${formatCurrency(totalSpent)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (trend.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Spending Trend (Last 3 Months)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                SpendingTrendChart(trend = trend)
            }
        }
    }
}

@Composable
fun SpendingTrendChart(trend: List<MonthlySpending>) {
    val maxAmount = trend.maxOfOrNull { it.amount } ?: 1.0
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width / trend.size.coerceAtLeast(1)
            
            trend.forEachIndexed { index, month ->
                val height = ((month.amount / maxAmount) * size.height).toFloat().coerceIn(0f, size.height)
                val x = index * barWidth + barWidth / 2
                val y = size.height - height
                
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(x - barWidth / 2 + 4.dp.toPx(), y),
                    size = androidx.compose.ui.geometry.Size(barWidth - 8.dp.toPx(), height)
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (month in trend) {
                Text(
                    text = month.month.substring(5), // Just the month part
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

