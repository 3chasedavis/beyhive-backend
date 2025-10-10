package com.beyhivealert.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.statusBarsPadding
import com.beyhivealert.android.R

@Composable
fun Header(
    onNavigateToSettings: () -> Unit = {},
    showSettingsButton: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .statusBarsPadding()
            .background(Color(0xFFEADFA7)) // Light yellow/beige background
    ) {
        // Yellow border at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFEADFA7)) // Same yellow as header
                .align(Alignment.TopCenter)
        )
        
        // Bee Icon on the left
        Icon(
            painter = painterResource(id = R.drawable.bee_icon),
            contentDescription = "Bee Icon",
            modifier = Modifier
                .size(32.dp)
                .padding(start = 16.dp)
                .align(Alignment.CenterStart),
            tint = Color.Unspecified
        )
        
        // Title in center
        Text(
            text = "Beyhive Alert",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
        
        // Settings button on the right (optional)
        if (showSettingsButton) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

