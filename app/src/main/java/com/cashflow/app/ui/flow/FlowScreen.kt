package com.cashflow.app.ui.flow

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.ui.timeline.TimelineScreen
import com.cashflow.app.ui.transactions.TransactionsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowScreen(
    repository: CashFlowRepository
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Timeline") },
                icon = { Icon(Icons.Default.DateRange, contentDescription = "Timeline") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Log") },
                icon = { Icon(Icons.Default.History, contentDescription = "Log") }
            )
        }
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> TimelineScreen(repository = repository)
                1 -> TransactionsScreen(repository = repository)
            }
        }
    }
}

