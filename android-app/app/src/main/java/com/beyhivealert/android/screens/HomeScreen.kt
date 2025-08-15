package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.R
import com.beyhivealert.android.components.InstagramFeedSection as InstagramFeedSectionComponent
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Partner
import com.beyhivealert.android.data.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import android.net.Uri

@Composable
fun HomeScreen(
    onNavigateToSurvivor: () -> Unit = {},
    onNavigateToDailyTrivia: () -> Unit = {},
    onNavigateToAlbumRanker: () -> Unit = {}
) {
    var showSettings by remember { mutableStateOf(false) }
    var showNonAffiliationSheet by remember { mutableStateOf(false) }
    var partners by remember { mutableStateOf<List<Partner>>(emptyList()) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoadingPartners by remember { mutableStateOf(true) }
    var isLoadingEvents by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Fetch partners and events when screen loads
    LaunchedEffect(Unit) {
        // Fetch partners
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val fetchedPartners = ApiService.fetchPartners()
                    partners = fetchedPartners
                    println("Fetched ${fetchedPartners.size} partners") // Debug log
                }
            } catch (e: Exception) {
                println("Error fetching partners: ${e.message}") // Debug log
                e.printStackTrace()
            } finally {
                isLoadingPartners = false
            }
        }
        
        // Fetch events
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val fetchedEvents = ApiService.fetchEvents()
                    events = fetchedEvents
                    println("Fetched ${fetchedEvents.size} events") // Debug log
                }
            } catch (e: Exception) {
                println("Error fetching events: ${e.message}") // Debug log
                e.printStackTrace()
            } finally {
                isLoadingEvents = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with bee icon and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFFEADFA7)) // New hex color #EADFA7
        ) {
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
            
            // Settings button on the right
            IconButton(
                onClick = { showSettings = true },
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
            
            // Refresh button (temporary for testing)
            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                val fetchedPartners = ApiService.fetchPartners()
                                partners = fetchedPartners
                                println("Manual refresh - Fetched ${fetchedPartners.size} partners")
                            }
                        } catch (e: Exception) {
                            println("Manual refresh error: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Beyoncé Non-Affiliation Banner
            item {
                Text(
                    text = "Beyoncé Non-Affiliation",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showNonAffiliationSheet = true }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            // Welcome Section
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bee_icon),
                        contentDescription = "Bee Icon",
                        modifier = Modifier.size(120.dp),
                        tint = Color.Unspecified
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Welcome to Beyhive Alert!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Stay connected to Beyoncé's tour and the Beyhive community.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Games Section
            item {
                Column {
                    // Games title with bee icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bee_icon),
                            contentDescription = "Bee Icon",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Unspecified
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Games",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    // Horizontal scrolling games cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        // Survivor Game Card
                        item {
                            GameCard(
                                title = "Survivor Game",
                                description = "Guess outfits, songs, and more during every show!",
                                icon = "🎮",
                                onClick = onNavigateToSurvivor
                            )
                        }
                        
                        // Daily Trivia Card
                        item {
                            GameCard(
                                title = "Daily Trivia",
                                description = "Test your Beyoncé knowledge with a new question every day!",
                                icon = "❓",
                                onClick = onNavigateToDailyTrivia
                            )
                        }
                        
                        // Album Ranker Card
                        item {
                            GameCard(
                                title = "Album Ranker",
                                description = "Rank Beyoncé's albums and see community favorites!",
                                icon = "🎵",
                                onClick = onNavigateToAlbumRanker
                            )
                        }
                    }
                }
            }
            
            // Partners Section
            item {
                Column {
                    // Partners title with bee icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bee_icon),
                            contentDescription = "Bee Icon",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Unspecified
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Partners",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    if (isLoadingPartners) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (partners.isEmpty()) {
                        Text(
                            text = "No partners available",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    } else {
                        // Partners cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(partners) { partner ->
                                PartnerCard(partner = partner)
                            }
                        }
                    }
                }
            }
            
            // Events Section
            item {
                Column {
                    // Events title with bee icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bee_icon),
                            contentDescription = "Bee Icon",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Unspecified
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Events",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    if (isLoadingEvents) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (events.isEmpty()) {
                        Text(
                            text = "No events available",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    } else {
                        // Events cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(events) { event ->
                                HomeEventCard(event = event)
                            }
                        }
                    }
                }
            }
            
            // Instagram Feed Section
            item {
                InstagramFeedSectionComponent()
            }
            
            // Divider
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(1.dp)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                }
            }
            
            // Space for bottom navigation
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    // TODO: Add sheets for settings and non-affiliation
    if (showSettings) {
        // TODO: Show settings dialog
    }
    
    if (showNonAffiliationSheet) {
        // TODO: Show non-affiliation sheet
    }
}

@Composable
fun GameCard(
    title: String,
    description: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(340.dp)
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = 0.7f),
                            Color.White,
                            Color.Blue.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 44.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PartnerCard(partner: Partner) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .width(340.dp)
            .height(140.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color.White,
                                Color.Blue
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.material.ripple.rememberRipple(bounded = true)
                    ) {
                        if (partner.link.isNotEmpty()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(partner.link))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handle invalid URL
                                println("Error opening partner link: ${e.message}")
                            }
                        }
                    }
            ) {
            // Small bee icon in top right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bee_icon),
                    contentDescription = "Bee Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (partner.iconUrl != null && partner.iconUrl.isNotEmpty()) {
                    // TODO: Load partner icon from URL
                    Icon(
                        painter = painterResource(id = R.drawable.bee_icon),
                        contentDescription = "Partner Icon",
                        modifier = Modifier.size(44.dp),
                        tint = Color.Black
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.bee_icon),
                        contentDescription = "Partner Icon",
                        modifier = Modifier.size(44.dp),
                        tint = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = partner.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                
                if (partner.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = partner.description,
                        fontSize = 13.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun HomeEventCard(event: Event) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = 0.7f),
                            Color.White,
                            Color.Blue.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )
                
                Column {
                    if (event.date.isNotEmpty()) {
                        Text(
                            text = "Date: ${event.date}",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                    
                    if (event.location.isNotEmpty()) {
                        Text(
                            text = "Location: ${event.location}",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                    
                    if (event.description.isNotEmpty()) {
                        Text(
                            text = event.description,
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
} 