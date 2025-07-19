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
import com.beyhivealert.android.ui.BeyhiveAlertTheme

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
                val response = ApiService.checkMaintenanceMode()
                isMaintenanceMode = response.isMaintenanceMode
            } catch (e: Exception) {
                // If maintenance check fails, continue with normal app
                isMaintenanceMode = false
            } finally {
                isLoading = false
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