package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.OutfitsViewModel
import com.beyhivealert.android.viewmodels.SetlistViewModel
import com.beyhivealert.android.data.Outfit
import com.beyhivealert.android.data.Setlist
import coil.compose.AsyncImage
import androidx.compose.ui.geometry.Offset

enum class TrackerTab { SETLIST, OUTFIT }

@Composable
fun TrackersScreen() {
    var selectedTab by remember { mutableStateOf(TrackerTab.SETLIST) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Tab Selection Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Song Tracker Button
            Button(
                onClick = { selectedTab = TrackerTab.SETLIST },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == TrackerTab.SETLIST) Color(0xFFEADFA7) else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Song Tracker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == TrackerTab.SETLIST) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            
            // Outfit Tracker Button
            Button(
                onClick = { selectedTab = TrackerTab.OUTFIT },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == TrackerTab.OUTFIT) Color(0xFFEADFA7) else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Outfit Tracker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == TrackerTab.OUTFIT) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
        
        // Main Content
        if (selectedTab == TrackerTab.SETLIST) {
            // Song Tracker Content
            SetlistView()
        } else {
            // Outfit Tracker Content
            OutfitsView()
        }
    }
}

@Composable
fun SetlistView() {
    val viewModel: SetlistViewModel = viewModel()
    val setlists by viewModel.setlists
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    Column {
        Text(
            text = "Trackers",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Track songs and outfits from each show.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Setlist",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else if (setlists.isEmpty()) {
            Text(
                text = "No setlists available",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // Setlist Cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(setlists) { setlist ->
                    SetlistCard(setlist = setlist)
                }
            }
        }
    }
}

@Composable
fun SetlistCard(setlist: Setlist) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEADFA7)), // Same yellow as header
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = setlist.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Black text for contrast on yellow
                modifier = Modifier.padding(bottom = 12.dp)
            )
            setlist.songs.sortedBy { it.order }.forEach { song ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Music note",
                        tint = Color.Black, // Black icon for contrast on yellow
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = song.name,
                        fontSize = 14.sp,
                        color = Color.Black // Black text for contrast on yellow
                    )
                }
            }
        }
    }
}

@Composable
fun OutfitsView() {
    val viewModel: OutfitsViewModel = viewModel()
    val outfits by viewModel.outfits
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    LaunchedEffect(Unit) {
        viewModel.fetchOutfits()
    }
    
    Column {
        Text(
            text = "Outfits",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else if (outfits.isEmpty()) {
            Text(
                text = "No outfits available",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(outfits) { outfit ->
                    OutfitCard(outfit = outfit)
                }
            }
        }
    }
}

@Composable
fun OutfitCard(outfit: Outfit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (outfit.imageUrl != null) {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Outfit placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = outfit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (outfit.description != null) {
                    Text(
                        text = outfit.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
} 