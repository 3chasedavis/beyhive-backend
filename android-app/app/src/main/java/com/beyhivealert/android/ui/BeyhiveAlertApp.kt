package com.beyhivealert.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beyhivealert.android.navigation.BeyhiveAlertNavigation
import com.beyhivealert.android.data.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.beyhivealert.android.ui.MaintenanceScreen
import com.beyhivealert.android.ui.theme.BeyhiveAlertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeyhiveAlertApp()
        }
    }
}

@Composable
fun BeyhiveAlertApp() {
    var isMaintenanceMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiService.fetchMaintenanceMode()
                isMaintenanceMode = response.isMaintenanceMode
                println("🔧 [DEBUG] Initial maintenance mode: ${response.isMaintenanceMode}")
            } catch (e: Exception) {
                // If maintenance check fails, continue with normal app
                isMaintenanceMode = false
                println("❌ [DEBUG] Initial maintenance check failed: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    // Check maintenance mode every 3 seconds (like iOS app)
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000) // 3 seconds
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiService.fetchMaintenanceMode()
                    if (isMaintenanceMode != response.isMaintenanceMode) {
                        println("🔄 [DEBUG] Maintenance mode changed! Updating UI.")
                    }
                    isMaintenanceMode = response.isMaintenanceMode
                } catch (e: Exception) {
                    // If maintenance check fails, continue with current state
                    println("❌ [DEBUG] Error checking maintenance mode: ${e.message}")
                }
            }
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (isMaintenanceMode) {
        MaintenanceScreen()
    } else {
        BeyhiveAlertTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                BeyhiveAlertNavigation()
            }
        }
    }
} 