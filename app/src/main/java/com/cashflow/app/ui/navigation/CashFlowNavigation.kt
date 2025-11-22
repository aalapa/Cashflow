package com.cashflow.app.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cashflow.app.di.AppModule
import com.cashflow.app.ui.accounts.AccountsScreen
import com.cashflow.app.ui.bills.BillsScreen
import com.cashflow.app.ui.income.IncomeScreen
import com.cashflow.app.ui.analyze.AnalyzeScreen
import com.cashflow.app.ui.auth.AuthScreen
import com.cashflow.app.ui.auth.AuthViewModel
import com.cashflow.app.ui.reset.ResetDataDialog
import com.cashflow.app.ui.settings.SettingsScreen
import com.cashflow.app.ui.settings.SettingsViewModel
import com.cashflow.app.ui.timeline.TimelineScreen
import com.cashflow.app.ui.transactions.TransactionsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object Auth : Screen("auth", "Auth", null)
    object Timeline : Screen("timeline", "Timeline", Icons.Default.DateRange)
    object Accounts : Screen("accounts", "Accounts", Icons.Default.AccountBalance)
    object Bills : Screen("bills", "Bills", Icons.Default.Receipt)
    object Income : Screen("income", "Income", Icons.Default.AttachMoney)
    object Transactions : Screen("transactions", "Log", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Analyze : Screen("analyze", "Analyze", Icons.Default.QueryStats)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowNavigation(
    onThemeChanged: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = AppModule.provideRepository(context)
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.state.collectAsState()
    
    val screens = listOf(
        Screen.Timeline,
        Screen.Accounts,
        Screen.Bills,
        Screen.Income,
        Screen.Transactions
    )
    
    // Get current dark theme preference
    val prefs = context.getSharedPreferences("cashflow_prefs", Context.MODE_PRIVATE)
    val initialDarkTheme = prefs.getBoolean("dark_theme", false)

    var showMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute }

    // Navigate to auth if not authenticated
    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated && currentRoute != Screen.Auth.route) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else if (authState.isAuthenticated && currentRoute == Screen.Auth.route) {
            navController.navigate(Screen.Timeline.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            if (authState.isAuthenticated && currentRoute != Screen.Auth.route) {
                TopAppBar(
                    title = { 
                        Text(
                            text = currentScreen?.title ?: "Cash Flow",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Analyze") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(Screen.Analyze.route)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.QueryStats, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(Screen.Settings.route)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null)
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Sign Out") },
                                onClick = {
                                    showMenu = false
                                    authViewModel.handleIntent(com.cashflow.app.ui.auth.AuthIntent.SignOut)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Reset All Data") },
                                onClick = {
                                    showMenu = false
                                    showResetDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (authState.isAuthenticated && currentRoute != Screen.Auth.route) {
                NavigationBar {
                    val navBackStackEntry = navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry.value?.destination
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState.isAuthenticated) Screen.Timeline.route else Screen.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Timeline.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Timeline.route) {
                TimelineScreen(repository = repository)
            }
            composable(Screen.Accounts.route) {
                AccountsScreen(repository = repository)
            }
            composable(Screen.Bills.route) {
                BillsScreen(repository = repository)
            }
            composable(Screen.Income.route) {
                IncomeScreen(repository = repository)
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(repository = repository)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = SettingsViewModel(
                        initialDarkTheme = initialDarkTheme,
                        onThemeChanged = onThemeChanged,
                        repository = repository
                    ),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Analyze.route) {
                AnalyzeScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        // Reset Data Dialog
        if (showResetDialog) {
            ResetDataDialog(
                onConfirm = {
                    scope.launch {
                        try {
                            repository.clearAllData()
                            showResetDialog = false
                            // Navigate back to timeline
                            navController.navigate(Screen.Timeline.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        } catch (e: Exception) {
                            // Handle error gracefully
                            showResetDialog = false
                        }
                    }
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}

