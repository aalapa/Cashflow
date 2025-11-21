package com.cashflow.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.cashflow.app.ui.theme.CashFlowTheme
import com.cashflow.app.ui.navigation.CashFlowNavigation

class MainActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("cashflow_prefs", MODE_PRIVATE)
        
        setContent {
            var isDarkTheme by remember {
                mutableStateOf(prefs.getBoolean("dark_theme", false))
            }
            
            CashFlowTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CashFlowNavigation(
                        onThemeChanged = { newValue ->
                            isDarkTheme = newValue
                            prefs.edit().putBoolean("dark_theme", newValue).apply()
                        }
                    )
                }
            }
        }
    }
}

