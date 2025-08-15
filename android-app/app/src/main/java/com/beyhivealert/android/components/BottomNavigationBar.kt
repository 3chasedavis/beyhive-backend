package com.beyhivealert.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.beyhivealert.android.R

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEADFA7), // New hex color #EADFA7
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab - House icon (keep the same)
            IconButton(
                onClick = { onTabSelected(0) },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(0.5.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color.White, // White color as requested
                    modifier = Modifier.size(40.dp) // Bigger icon as requested
                )
            }

            // Videos Tab - Video camera icon
            IconButton(
                onClick = { onTabSelected(1) },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(0.5.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow, // Using PlayArrow for videos
                    contentDescription = "Videos",
                    tint = Color.White, // White color as requested
                    modifier = Modifier.size(40.dp) // Bigger icon as requested
                )
            }

            // Center Bee Icon (Notifications tab) - Yellow circle with bee icon
            IconButton(
                onClick = { onTabSelected(2) },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEB3B)) // Yellow background
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bee_icon),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(40.dp), // Made bigger from 32dp to 40dp
                    tint = Color.Unspecified
                )
            }

            // Trackers Tab - Favorite icon (represents favorite songs/tracks for singers)
            IconButton(
                onClick = { onTabSelected(3) },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(0.5.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite, // Using Favorite which represents favorites/tracks
                    contentDescription = "Trackers",
                    tint = Color.White, // White color as requested
                    modifier = Modifier.size(40.dp) // Bigger icon as requested
                )
            }

            // Calendar Tab - Calendar icon
            IconButton(
                onClick = { onTabSelected(4) },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(0.5.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.White, // White color as requested
                    modifier = Modifier.size(40.dp) // Bigger icon as requested
                )
            }
        }
    }
} 