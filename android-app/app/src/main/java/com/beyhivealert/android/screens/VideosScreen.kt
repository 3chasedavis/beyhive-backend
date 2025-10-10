package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.LivestreamsViewModel
import com.beyhivealert.android.data.Livestream
import com.beyhivealert.android.R
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.delay

@Composable
fun VideosScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val viewModel: LivestreamsViewModel = viewModel()
    val livestreams by viewModel.livestreams
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val context = LocalContext.current

    // Timer for countdown updates
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000) // Update every second
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.fetchLivestreams()
        viewModel.fetchCountdownMode()
        viewModel.fetchNextShowDate()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Text(
            text = "Livestreams",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error loading livestreams",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = error ?: "Unknown error",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.fetchLivestreams() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        } else {
            if (livestreams.isEmpty()) {
                // Show countdown or empty state centered
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Countdown Timer (only show when enabled)
                        if (viewModel.isCountdownEnabled.value && viewModel.countdownString.isNotEmpty()) {
                            CountdownTimerCard(countdownString = viewModel.countdownString)
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                        
                        EmptyStateContent(context = context)
                    }
                }
            } else {
                // Show livestreams in a list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(livestreams) { livestream ->
                        LivestreamCard(livestream = livestream)
                    }
                    
                    // Space for bottom navigation
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CountdownTimerCard(countdownString: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Next Show",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = countdownString,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = 0.7f),
                            Color.White,
                            Color.Blue.copy(alpha = 0.7f)
                        )
                    ),
                    RoundedCornerShape(12.dp)
                )
        )
    }
}

@Composable
fun EmptyStateContent(context: android.content.Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Main message
        Text(
            text = "Check back during the next show for livestreams!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Bee Icon and Links section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bee_icon),
                contentDescription = "Bee Icon",
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Links",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF21274A)
            )
        }
        
        // Games button
        Button(
            onClick = {
                // TODO: Navigation will be implemented in a future update
                // For now, this button shows the intent to navigate to games/home
            },
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red.copy(alpha = 0.7f),
                                Color.White,
                                Color.Blue.copy(alpha = 0.7f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "Play our games now!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF21274A)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Go",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Twitter/X button
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com/beyhivealertapp?s=21"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red.copy(alpha = 0.7f),
                                Color.White,
                                Color.Blue.copy(alpha = 0.7f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "Follow us on Twitter/X",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF21274A)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Open",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun LivestreamCard(livestream: Livestream) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Platform icon
            val iconRes = when (livestream.platform.lowercase()) {
                "tiktok" -> R.drawable.tiktoklogo
                "instagram" -> R.drawable.instagramlogo
                "youtube" -> R.drawable.youtubelogo
                "discord" -> R.drawable.discordlogo
                "x" -> R.drawable.xlogo
                "other" -> R.drawable.bee_icon
                else -> R.drawable.bee_icon // fallback
            }
            
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = livestream.platform,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (livestream.title.isEmpty()) livestream.platform else livestream.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (!livestream.title.isEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = livestream.platform,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Watch button
            Button(
                onClick = {
                    var link = livestream.url
                    if (!link.lowercase().startsWith("http")) {
                        link = "https://$link"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF260) // Light yellow
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Watch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}
