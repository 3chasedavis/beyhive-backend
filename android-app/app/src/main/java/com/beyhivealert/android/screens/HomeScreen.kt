package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.components.TopBar
import com.beyhivealert.android.components.InstagramFeedSection
import com.beyhivealert.android.components.GamesSection
import com.beyhivealert.android.components.NewsSection

@Composable
fun HomeScreen() {
    var showSettings by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Changed from yellow to white
    ) {
        TopBar(
            title = "Beyhive Alert",
            onSettingsClick = { showSettings = true }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                InstagramFeedSection()
            }
            
            item {
                GamesSection()
            }
            
            item {
                NewsSection()
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
            }
        }
    }
    
    if (showSettings) {
        // TODO: Show settings dialog
    }
} 