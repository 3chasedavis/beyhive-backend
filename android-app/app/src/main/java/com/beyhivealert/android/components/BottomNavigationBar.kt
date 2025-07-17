package com.beyhivealert.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        color = Color(0xFFFFE082), // Darker yellow background for better contrast
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            BottomNavIcon(
                icon = Icons.Filled.Home,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            // Videos Tab
            BottomNavIcon(
                icon = Icons.Filled.PlayArrow,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )

            // Center Bee Icon (not clickable)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEB3B)) // Yellow
                    .offset(y = (-12).dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bee_icon),
                    contentDescription = "Bee",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }

            // Notifications Tab
            BottomNavIcon(
                icon = Icons.Filled.Notifications,
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )

            // Calendar Tab
            BottomNavIcon(
                icon = Icons.Filled.DateRange,
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
private fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
} 