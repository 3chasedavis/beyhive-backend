package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import com.beyhivealert.android.data.ApiService
import android.os.Handler
import android.os.Looper

@Composable
fun NotificationsScreen() {
    var showConfirmation by remember { mutableStateOf(false) }
    var confirmationText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var pendingToggleIndex by remember { mutableStateOf<Int?>(null) }
    
    // Removed scope since we're not using coroutines for Firebase token
    
    // Real game tiles that match your backend structure
    var gameTiles by remember {
        mutableStateOf(
            listOf(
                GameTile("Beyoncé on Stage", "🎭", true, "beyonceOnStage"),
                GameTile("Concert Start", "🎪", true, "concertStart"),
                GameTile("AMERICA HAS A PROBLEM starts", "🇺🇸", true, "americaHasAProblem"),
                GameTile("TYRANT starts", "👑", true, "tyrant"),
                GameTile("Last Act starts", "🎬", true, "lastAct"),
                GameTile("16 CARRIAGES starts", "🐎", true, "sixteenCarriages"),
                GameTile("AMEN starts", "🙏", true, "amen")
            )
        )
    }
    
    // Fetch notification preferences from backend when screen loads
    LaunchedEffect(Unit) {
        try {
            // Get FCM token (this is not a coroutine, so we need to handle it differently)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    // Fetch preferences once we have the token
                    if (token != null) {
                        val preferences = ApiService.fetchNotificationPreferences(token)
                        // Update game tiles with backend preferences
                        gameTiles = gameTiles.map { tile ->
                            val isOn = preferences[tile.preferenceKey] ?: true
                            tile.copy(isOn = isOn)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error fetching notification preferences: ${e.message}")
            // Keep default values if fetch fails
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFFEADFA7)) // New hex color #EADFA7
        ) {
            Text(
                text = "Notifications",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Main content area with better centering
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Game tiles grid with better centering
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(gameTiles) { index, tile ->
                        GameTileCard(
                            tile = tile,
                            isLoading = isLoading && pendingToggleIndex == index,
                            onClick = {
                                // Start loading and track which tile is pending
                                isLoading = true
                                pendingToggleIndex = index
                                
                                // Get FCM token and handle the click
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result
                                        
                                        // Update the tile locally
                                        val newTiles = gameTiles.toMutableList()
                                        newTiles[index] = newTiles[index].copy(isOn = !newTiles[index].isOn)
                                        gameTiles = newTiles
                                        
                                        // Convert to backend format
                                        val preferences = newTiles.associate { tile ->
                                            tile.preferenceKey to tile.isOn
                                        }
                                        
                                        // Send to backend
                                        val success = ApiService.updateNotificationPreferences(token, preferences)
                                        
                                        if (success) {
                                            // Show confirmation
                                            confirmationText = if (newTiles[index].isOn) {
                                                "Enabled for ${tile.title}"
                                            } else {
                                                "Disabled for ${tile.title}"
                                            }
                                            showConfirmation = true
                                        } else {
                                            // Revert on failure
                                            gameTiles = gameTiles.toMutableList().apply {
                                                this[index] = this[index].copy(isOn = !this[index].isOn)
                                            }
                                            errorMessage = "Failed to save preferences. Please try again."
                                            showError = true
                                        }
                                        
                                        // Reset loading state
                                        isLoading = false
                                        pendingToggleIndex = null
                                        
                                        // Hide confirmation after delay
                                        if (showConfirmation) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                showConfirmation = false
                                            }, 1500)
                                        }
                                    } else {
                                        // Handle token failure
                                        errorMessage = "Failed to get device token"
                                        showError = true
                                        isLoading = false
                                        pendingToggleIndex = null
                                    }
                                }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Info text with better centering
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Tiles in color are on, tiles in gray are off.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // Confirmation message
                if (showConfirmation) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = confirmationText,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Error alert
    if (showError) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun GameTileCard(
    tile: GameTile,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(
                enabled = !isLoading,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    when {
                        isLoading -> Modifier.background(Color.Gray.copy(alpha = 0.3f))
                        tile.isOn -> Modifier.background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red.copy(alpha = 0.7f),
                                    Color.White,
                                    Color.Blue.copy(alpha = 0.7f)
                                )
                            )
                        )
                        else -> Modifier.background(Color.Gray)
                    }
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tile.icon,
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = tile.title,
                    fontSize = 10.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class GameTile(
    val title: String,
    val icon: String,
    val isOn: Boolean,
    val preferenceKey: String = ""
)
