package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.LivestreamsViewModel
import com.beyhivealert.android.data.Livestream
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue

@Composable
fun VideosScreen() {
    val viewModel: LivestreamsViewModel = viewModel()
    val livestreams by viewModel.livestreams
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFFFF7CC)) // Changed back to yellow to match iOS
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bee_icon),
                contentDescription = "Bee Icon",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Beyhive Alert",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { /* TODO: Settings */ },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Livestreams",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Unknown error", color = Color.Red)
                }
            }
            livestreams.isEmpty() -> {
                Text(
                    text = "Check back during the next show for livestreams!",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    livestreams.forEach { stream ->
                        LivestreamCard(stream)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Links Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bee_icon),
                    contentDescription = "Bee Icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Links",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22223B)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(
                text = "Play our games now!",
                buttonText = "Go",
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(
                text = "Follow us on Twitter/X",
                buttonText = "Open",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF6B35), // Red
                        Color.White,        // White
                        Color(0xFF4FC3F7)  // Blue
                    )
                )
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Changed to black for better contrast on white gradient
            )
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7) // Light blue
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LivestreamCard(stream: Livestream) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stream.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = stream.platform,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stream.url))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Watch Now")
            }
        }
    }
} 