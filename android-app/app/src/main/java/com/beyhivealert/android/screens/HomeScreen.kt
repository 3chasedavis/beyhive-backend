package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
// Using only Notifications icon for simplicity
import androidx.compose.material3.*
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import com.beyhivealert.android.R
import com.beyhivealert.android.components.InstagramFeedSection as InstagramFeedSectionComponent
import com.beyhivealert.android.components.Header
import com.beyhivealert.android.data.ApiService
import com.beyhivealert.android.data.Partner
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.SharedPreferences
// Removed AsyncImage; partners use bee icon

// Function to check notification permission status
fun checkNotificationPermissionStatus(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.areNotificationsEnabled()
    }
}

// Function to check if user has already been asked for notification permission
fun hasUserBeenAskedForNotificationPermission(context: android.content.Context): Boolean {
    val prefs = context.getSharedPreferences("notification_prefs", android.content.Context.MODE_PRIVATE)
    return prefs.getBoolean("has_been_asked", false)
}

// Function to mark that user has been asked for notification permission
fun markUserAskedForNotificationPermission(context: android.content.Context) {
    val prefs = context.getSharedPreferences("notification_prefs", android.content.Context.MODE_PRIVATE)
    prefs.edit().putBoolean("has_been_asked", true).apply()
}

@Composable
fun HomeScreen(
    onNavigateToSurvivor: () -> Unit = {},
    onNavigateToDailyTrivia: () -> Unit = {},
    onNavigateToAlbumRanker: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var showNonAffiliationSheet by remember { mutableStateOf(false) }
    var partners by remember { mutableStateOf<List<Partner>>(emptyList()) }
    var isLoadingPartners by remember { mutableStateOf(true) }
    
    // Snackbar state
    var snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    
    // Notification permission loading state
    var isRequestingNotificationPermission by remember { mutableStateOf(false) }
    
    // Bottom popup state - show when notifications are disabled and user hasn't granted permission
    var showNotificationPopup by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Notification permission stateus
    var hasNotificationPermission by remember { 
        mutableStateOf(checkNotificationPermissionStatus(context))
    }
    
    // Function to register FCM token with backend
    val registerTokenWithBackend = { token: String ->
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val url = java.net.URL("https://beyhive-backend.onrender.com/register-device")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    
                    val body = mapOf(
                        "deviceToken" to token,
                        "platform" to "android",
                        "preferences" to mapOf(
                            "beyonceOnStage" to true,
                            "concertStart" to true,
                            "americaHasAProblem" to true,
                            "tyrant" to true,
                            "lastAct" to true,
                            "sixteenCarriages" to true,
                            "amen" to true
                        )
                    )
                    
                    val jsonBody = org.json.JSONObject(body).toString()
                    connection.outputStream.use { os ->
                        os.write(jsonBody.toByteArray())
                        os.flush()
                    }
                    
                    val responseCode = connection.responseCode
                    println("Token registration response code: $responseCode")
                    
                    if (responseCode == 200) {
                        println("✅ FCM token successfully registered with backend")
                    } else {
                        println("❌ Failed to register FCM token with backend: $responseCode")
                        try {
                            val errorStream = connection.errorStream
                            if (errorStream != null) {
                                val errorResponse = errorStream.bufferedReader().readText()
                                println("Error response: $errorResponse")
                            }
                        } catch (e: Exception) {
                            println("Could not read error response: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ Error registering FCM token with backend: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    // Function to fetch and register FCM token with backend
    val fetchAndRegisterFCMToken = {
        println("=== FETCHING FCM TOKEN ===")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("✅ FCM Token obtained: $token")
                if (token != null) {
                    // Register token with backend
                    registerTokenWithBackend(token)
                }
            } else {
                println("❌ Failed to get FCM token: ${task.exception?.message}")
            }
        }
    }
    
    // Function to open app notification settings
    val openNotificationSettings = {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general app settings
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(fallbackIntent)
        }
    }
    
    // Permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        println("=== PERMISSION RESULT ===")
        println("Permission granted: $isGranted")
        isRequestingNotificationPermission = false
        hasNotificationPermission = isGranted
        
        // Mark that user has been asked for permission
        markUserAskedForNotificationPermission(context)
        
        if (isGranted) {
            // Hide popup and show success message
            println("✅ Notification permission GRANTED - hiding popup")
            showNotificationPopup = false
            // Fetch and register FCM token now that permission is granted
            fetchAndRegisterFCMToken()
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Notifications enabled! You'll now receive tour updates and alerts.",
                    duration = SnackbarDuration.Short
                )
            }
        } else {
            // Show message about enabling notifications manually
            println("❌ Notification permission DENIED - showing settings option")
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Notifications disabled. Tap to open settings and enable them manually.",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Open Settings"
                )
                if (result == SnackbarResult.ActionPerformed) {
                    openNotificationSettings()
                }
            }
        }
        println("========================")
    }
    
    // Function to request notification permission
    val requestNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            println("=== REQUESTING NOTIFICATION PERMISSION ===")
            println("Launching Android permission dialog...")
            isRequestingNotificationPermission = true
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            println("Android version < 13, notification permission not required")
        }
    }
    
    // Function to refresh permission status (useful when returning from settings)
    val refreshPermissionStatus = {
        val newStatus = checkNotificationPermissionStatus(context)
        hasNotificationPermission = newStatus
        if (newStatus) {
            showNotificationPopup = false
            // Fetch and register FCM token when permission is granted
            fetchAndRegisterFCMToken()
        } else {
            // Show popup if permission is not granted (persistent)
            showNotificationPopup = true
        }
    }
    
    // Refresh permission status when app resumes
    LaunchedEffect(Unit) {
        // This will run when the composable is first created
    }
    
    // Listen for app lifecycle changes to refresh permission status
    DisposableEffect(Unit) {
        val lifecycleObserver = object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                refreshPermissionStatus()
            }
        }
        
        val lifecycle = (context as? androidx.lifecycle.LifecycleOwner)?.lifecycle
        lifecycle?.addObserver(lifecycleObserver)
        
        onDispose {
            lifecycle?.removeObserver(lifecycleObserver)
        }
    }
    
    // Fetch partners and events when screen loads
    LaunchedEffect(Unit) {
        // Check notification permission status
        val currentPermissionStatus = checkNotificationPermissionStatus(context)
        hasNotificationPermission = currentPermissionStatus
        
        // If user has permission but we haven't marked them as asked, mark them now
        if (currentPermissionStatus && !hasUserBeenAskedForNotificationPermission(context)) {
            markUserAskedForNotificationPermission(context)
        }
        
        // If user already has permission, fetch FCM token
        if (currentPermissionStatus) {
            fetchAndRegisterFCMToken()
        }
        
        // Debug logging
        println("=== NOTIFICATION PERMISSION DEBUG ===")
        println("Android Version: ${Build.VERSION.SDK_INT}")
        println("TIRAMISU: ${Build.VERSION_CODES.TIRAMISU}")
        println("Has Permission: $currentPermissionStatus")
        println("Has Been Asked: ${hasUserBeenAskedForNotificationPermission(context)}")
        println("Will Show Popup: ${Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !currentPermissionStatus && !hasUserBeenAskedForNotificationPermission(context)}")
        println("=====================================")
        
        // Show notification popup if permission is not granted (Android 13+) - persistent until granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!currentPermissionStatus) {
                // Delay slightly to let the UI settle
                kotlinx.coroutines.delay(1000)
                showNotificationPopup = true
                println("Showing notification popup - permission not granted")
            } else {
                println("Not showing notification popup - permission already granted")
            }
        } else {
            println("Not showing notification popup - Android version < 13")
        }
        
        // Fetch partners
        scope.launch {
            try {
                val fetchedPartners = ApiService.fetchPartners()
                partners = fetchedPartners
                println("=== PARTNERS FETCH DEBUG ===")
                println("Timestamp: ${System.currentTimeMillis()}")
                println("Fetched ${fetchedPartners.size} partners")
                println("Backend URL: https://beyhive-backend.onrender.com/api/partners")
                // Debug: Print raw partner data to see what we're getting
                fetchedPartners.forEach { partner ->
                    println("=== PARTNER DEBUG ===")
                    println("Name: ${partner.name}")
                    println("Icon URL: ${partner.iconUrl}")
                    println("Description: ${partner.description}")
                    println("Link: ${partner.link}")
                    println("==================")
                }
                println("=== END PARTNERS DEBUG ===")
            } catch (e: Exception) {
                println("Error fetching partners: ${e.message}") // Debug log
                e.printStackTrace()
            } finally {
                isLoadingPartners = false
            }
        }
        

    }
    
    // Removed refreshNotificationPermissionStatus - no longer needed with new popup design
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEADFA7)) // Match header color
    ) {
        Header(onNavigateToSettings = onNavigateToSettings)
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // White background for content area
            contentPadding = PaddingValues(vertical = 16.dp),
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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Notification popup now appears automatically at the bottom
                }
            }
            
            // Notification banner removed - using bottom popup instead
            
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
                        contentPadding = PaddingValues(0.dp)
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
                        // Partners cards - using Row instead of LazyRow to prevent scrolling conflicts
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(0.dp)
                        ) {
                            partners.forEach { partner ->
                                PartnerCard(partner = partner)
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
        
        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp)
        )
    }
    
    // Bottom Notification Permission Popup
    if (showNotificationPopup && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Semi-transparent overlay (non-clickable to make popup persistent)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFD32F2F), // Red
                                    Color.White,        // White
                                    Color(0xFF2196F3)   // Blue
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Notification icon with subtle background
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                modifier = Modifier.size(32.dp),
                                tint = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Stay in the Hive! 🐝",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Get instant tour updates, exclusive alerts, and never miss a moment with Beyoncé",
                            fontSize = 15.sp,
                            color = Color.Black.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Allow button (full width, persistent popup)
                        Button(
                            onClick = {
                                isRequestingNotificationPermission = true
                                requestNotificationPermission()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isRequestingNotificationPermission
                        ) {
                            if (isRequestingNotificationPermission) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Allow Notifications",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Non-affiliation sheet
    if (showNonAffiliationSheet) {
        AlertDialog(
            onDismissRequest = { showNonAffiliationSheet = false },
            title = { 
                Text(
                    text = "Beyoncé Non-Affiliation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = { 
                Text(
                    text = "Beyhive Alert is an aggregation of publicly available information and is committed to accuracy, but is not responsible for inaccurate notifications. Beyhive Alert has no affiliation, association, endorsement, or any connection with Beyoncé, or any subsidiaries or affiliates including but not limited to the COWBOY CARTER Tour. Song titles and setlist information are used for informational purposes only. To support Beyoncé, please visit the official Beyoncé website at https://www.beyonce.com/ and the official COWBOY CARTER Tour website.",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showNonAffiliationSheet = false }
                ) {
                    Text(
                        text = "Close",
                        color = Color.Blue,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
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
            // Removed small bee icon in top-right corner
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display partner image - handle base64 data from backend
                if (partner.iconUrl != null && partner.iconUrl.isNotEmpty()) {
                    // Check if it's base64 data (starts with data:image or is a long base64 string)
                    if (partner.iconUrl.startsWith("data:image") || partner.iconUrl.length > 100) {
                        // Base64 image data - use a state to handle the bitmap
                        val bitmapState = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                        
                        LaunchedEffect(partner.iconUrl) {
                            try {
                                val base64String = if (partner.iconUrl.startsWith("data:image")) {
                                    partner.iconUrl.substring(partner.iconUrl.indexOf(",") + 1)
                                } else {
                                    partner.iconUrl
                                }
                                
                                val imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                bitmapState.value = bitmap
                            } catch (e: Exception) {
                                // Keep bitmapState.value as null
                            }
                        }
                        
                        if (bitmapState.value != null) {
                            Image(
                                bitmap = bitmapState.value!!.asImageBitmap(),
                                contentDescription = "Partner Icon",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            // Show placeholder while loading or if failed
                            Icon(
                                painter = painterResource(id = R.drawable.bee_icon),
                                contentDescription = "Partner Icon",
                                modifier = Modifier.size(44.dp),
                                tint = Color.Unspecified
                            )
                        }
                    } else {
                        // Regular URL image
                        AsyncImage(
                            model = partner.iconUrl,
                            contentDescription = "Partner Icon",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        )
                    }
                } else {
                    // Fallback to bee icon
                    Icon(
                        painter = painterResource(id = R.drawable.bee_icon),
                        contentDescription = "Partner Icon",
                        modifier = Modifier.size(44.dp),
                        tint = Color.Unspecified
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

 