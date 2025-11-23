package com.cashflow.app.ui.envelopes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconForString(iconName: String): ImageVector {
    return when (iconName) {
        "Folder" -> Icons.Default.Folder
        "Fastfood" -> Icons.Default.Fastfood
        "Home" -> Icons.Default.Home
        "Car" -> Icons.Default.DirectionsCar
        "School" -> Icons.Default.School
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "LocalGasStation" -> Icons.Default.LocalGasStation
        "Movie" -> Icons.Default.Movie
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "HealthAndSafety" -> Icons.Default.HealthAndSafety
        else -> Icons.Default.Folder
    }
}

val predefinedColors = listOf(
    Color(0xFF7C3AED), // Purple
    Color(0xFF0891B2), // Teal
    Color(0xFFEC4899), // Pink
    Color(0xFF10B981), // Green
    Color(0xFFEF4444), // Red
    Color(0xFFF59E0B), // Amber
    Color(0xFF3B82F6), // Blue
    Color(0xFF8B5CF6), // Violet
    Color(0xFF06B6D4), // Cyan
    Color(0xFFF97316)  // Orange
)

val predefinedIcons = listOf(
    "Folder", "Fastfood", "Home", "Car", "School", "ShoppingCart",
    "LocalGasStation", "Movie", "FitnessCenter", "HealthAndSafety"
)

