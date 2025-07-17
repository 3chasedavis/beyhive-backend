package com.beyhivealert.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val viewModel = remember { InstagramFeedViewModel() }
    val sections by viewModel.sections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadFeeds()
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Beyoncé Updates Section
        InstagramSectionHeader(
            title = "Beyoncé Updates",
            username = "@beyonceupdatesz"
        )
        
        // Arionce Section
        InstagramSectionHeader(
            title = "Arionce",
            username = "@arionce.lifee"
        )
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
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
                AsyncImage(
                    model = profileImageAsset,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Text(
                    text = username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { /* TODO: Open profile URL */ }
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
                onClick = { /* TODO: Open post URL */ }
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