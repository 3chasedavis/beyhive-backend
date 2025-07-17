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
fun TrackersScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            title = "Trackers",
            onSettingsClick = { /* TODO: Show settings */ }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Tour Notifications",
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
                            text = "Notification Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Customize which tour events you want to be notified about.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        // TODO: Add notification toggles
                        Row(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Beyoncé on Stage",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = false,
                                onCheckedChange = { /* TODO: Toggle notification */ }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Concert Start",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = false,
                                onCheckedChange = { /* TODO: Toggle notification */ }
                            )
                        }
                    }
                }
            }
        }
    }
} 