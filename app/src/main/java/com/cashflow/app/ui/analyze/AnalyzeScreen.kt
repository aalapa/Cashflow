package com.cashflow.app.ui.analyze

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.domain.repository.CashFlowRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(
    repository: CashFlowRepository,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: AnalyzeViewModel = viewModel { AnalyzeViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cash Flow Analysis",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Description
            item {
                Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Optimize Credit Card Payments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This analysis will find the optimal credit card payment amounts to keep your cash flow positive over the next ${state.analysisTimePeriodDays} days.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                }
            }

            // Time Period Selector
            item {
                Column {
                    Text(
                        text = "Analysis Period",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(30, 60, 90, 180).forEach { days ->
                            FilterChip(
                                selected = state.analysisTimePeriodDays == days,
                                onClick = { viewModel.handleIntent(AnalyzeIntent.SetTimePeriod(days)) },
                                label = { Text("${days}D") },
                                modifier = Modifier.weight(1f),
                                enabled = !state.isAnalyzing
                            )
                        }
                    }
                }
            }

            // Start Analysis Button
            if (!state.isComplete && !state.isAnalyzing) {
                item {
                    Button(
                        onClick = { viewModel.handleIntent(AnalyzeIntent.StartAnalysis) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isAnalyzing
                    ) {
                        Text("Start Analysis")
                    }
                }
            }

            // Loading State
            if (state.isAnalyzing) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing credit card payments...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Error State
            if (state.error != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Results
            if (state.isComplete && state.recommendations.isNotEmpty()) {
                // Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "✓ Analysis Complete",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "You can save $${String.format("%.2f", state.totalSavings)} per cycle while staying cash flow positive!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Recommendations Header
                item {
                    Text(
                        text = "Recommended Payment Amounts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Recommendations List
                items(state.recommendations) { recommendation ->
                    RecommendationCard(recommendation)
                }

                // Reset Button
                item {
                    Button(
                        onClick = { viewModel.handleIntent(AnalyzeIntent.Reset) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Run New Analysis")
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: CreditCardRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recommendation.bill.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Original",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", recommendation.originalAmount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    Icons.Default.TrendingDown,
                    contentDescription = "Reduced to",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Recommended",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", recommendation.recommendedAmount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }

            if (recommendation.savings > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Savings per cycle:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", recommendation.savings)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

