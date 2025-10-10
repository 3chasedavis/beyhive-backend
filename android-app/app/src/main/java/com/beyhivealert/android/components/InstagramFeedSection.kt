package com.beyhivealert.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.beyhivealert.android.R
import com.beyhivealert.android.data.InstagramFeedItem
import com.beyhivealert.android.data.InstagramFeedSection
import com.beyhivealert.android.viewmodels.InstagramFeedViewModel

@Composable
fun InstagramFeedSection() {
    println("=== INSTAGRAM FEED SECTION DEBUG ===")
    println("InstagramFeedSection Composable called")
    
    val viewModel = remember { 
        println("Creating new InstagramFeedViewModel")
        InstagramFeedViewModel() 
    }
    val sections by viewModel.sections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    println("Current state - isLoading: $isLoading, sections: ${sections.size}, error: $errorMessage")

    LaunchedEffect(Unit) {
        println("LaunchedEffect triggered - calling viewModel.loadFeeds()")
        viewModel.loadFeeds()
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Failed to load feeds",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // Display each section with its feed items
            sections.forEach { section ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header
                    InstagramSectionHeader(
                        title = section.title,
                        username = section.username
                    )
                    
                    // Feed items for this section
                    if (section.items.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 600.dp), // Limit height to prevent infinite constraints
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Show only first 2 items to prevent too much content
                            section.items.take(2).forEach { item ->
                                InstagramFeedCard(
                                    item = item,
                                    username = section.username,
                                    profileImageAsset = section.profileImageAsset,
                                    profileURL = section.profileURL
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No posts available",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstagramSectionHeader(
    title: String,
    username: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = username,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun InstagramFeedCard(
    item: InstagramFeedItem,
    username: String,
    profileImageAsset: String,
    profileURL: String
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with profile and username
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Use local asset for profile image
                val resourceId = context.resources.getIdentifier(
                    profileImageAsset, 
                    "drawable", 
                    context.packageName
                )
                
                if (resourceId != 0) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = resourceId),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to bee icon if asset not found
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.bee_icon),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Text(
                    text = username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { 
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(profileURL))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            println("Error opening profile URL: ${e.message}")
                        }
                    }
                ) {
                    Text("See Profile")
                }
            }
            
            // Post image
            AsyncImage(
                model = item.postImageURL,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Post link
            TextButton(
                onClick = { 
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(item.postURL))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        println("Error opening post URL: ${e.message}")
                    }
                }
            ) {
                Text("View more on Instagram")
            }
            
            // Description
            Text(
                text = item.description,
                fontSize = 14.sp
            )
        }
    }
} 