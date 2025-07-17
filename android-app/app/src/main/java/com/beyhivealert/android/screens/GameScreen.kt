package com.beyhivealert.android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.components.TopBar

@Composable
fun GameScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            title = "Album Ranking",
            onSettingsClick = { /* TODO: Show settings */ }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Rank Beyoncé's Albums",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Album Ranking Game",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Drag and drop to rank Beyoncé's albums from your favorite to least favorite!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        Button(
                            onClick = { /* TODO: Start game */ },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Start Game")
                        }
                    }
                }
            }
        }
    }
} 