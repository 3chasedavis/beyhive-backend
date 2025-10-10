package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.R
import com.beyhivealert.android.components.Header
import android.content.Intent
import android.net.Uri
import android.content.SharedPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast
import com.beyhivealert.android.data.ApiService

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BeyhiveAlertPrefs", 0)
    val scope = rememberCoroutineScope()
    
    var username by remember { mutableStateOf(sharedPreferences.getString("username", "") ?: "") }
    var email by remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var altEmail by remember { mutableStateOf(sharedPreferences.getString("altEmail", "") ?: "") }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEADFA7)) // Match header color
    ) {
        Header(
            onNavigateToSettings = { onNavigateBack() },
            showSettingsButton = false
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Light gray background for content
        ) {
            Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Empty space to center the title
                Spacer(modifier = Modifier.width(56.dp))
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sign Up / Log In",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "Enter your email and an optional alternate email so we can contact you if you win a prize in the Survivor game. This is optional and only required if you want to claim a prize.",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                    
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Custom Username") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 9.sp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 9.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    OutlinedTextField(
                        value = altEmail,
                        onValueChange = { altEmail = it },
                        label = { Text("Alternate Email (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 9.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Button(
                        onClick = {
                            sharedPreferences.edit().apply {
                                putString("username", username)
                                putString("email", email)
                                putString("altEmail", altEmail)
                                apply()
                            }
                            saveMessage = "Contact info saved!"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Save Changes",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    
                    saveMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 10.sp,
                            color = Color.Green
                        )
                    }
                }
            }
            
            // Settings options
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsRow(
                        title = "Share our app",
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out Beyhive Alert! https://play.google.com/store/apps/details?id=com.beyhivealert.android")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        }
                    )
                    
                    Divider(modifier = Modifier.padding(start = 16.dp))
                    
                    SettingsRow(
                        title = "Privacy Policy & Terms of Service",
                        onClick = onNavigateToPrivacyPolicy
                    )
                    
                    Divider(modifier = Modifier.padding(start = 16.dp))
                    
                    SettingsRow(
                        title = "Restore Purchases",
                        onClick = {
                            // Open Google Play subscriptions / restore purchases help
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
                            context.startActivity(intent)
                        }
                    )
                    
                    Divider(modifier = Modifier.padding(start = 16.dp))
                    
                    SettingsRow(
                        title = "Delete My Account",
                        isDestructive = true,
                        onClick = {
                            showDeleteAlert = true
                        }
                    )
                }
            }
            
            // Support/help
            Text(
                text = "For additional help:\nbeyhivealert@gmail.com",
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Social icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/beyhivealert/"))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.instagramlogo),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@beyhive.alert"))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.tiktoklogo),
                        contentDescription = "TikTok",
                        modifier = Modifier.size(54.dp),
                        tint = Color.Unspecified
                    )
                }
            }
            
            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Beyhive Alert Affiliation Disclaimer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "Beyhive Alert is an aggregation of publicly available information and is committed to accuracy, but is not responsible for inaccurate notifications. Beyhive Alert has no affiliation, association, endorsement, or any connection with Beyoncé, or any subsidiaries or affiliates including but not limited to the COWBOY CARTER Tour. Song titles and setlist information are used for informational purposes only. To support Beyoncé, please visit the official Beyoncé website at https://www.beyonce.com/ and the official COWBOY CARTER Tour website.",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    
    // Delete account alert
    if (showDeleteAlert) {
        AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Attempt backend account deletion if auth token exists
                        val authToken = sharedPreferences.getString("authToken", null)
                        // Note: Account deletion would require backend implementation
                        // Clear all local data
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        showDeleteAlert = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAlert = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Auto-hide save message after 2 seconds
    LaunchedEffect(saveMessage) {
        if (saveMessage != null) {
            kotlinx.coroutines.delay(2000)
            saveMessage = null
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDestructive) Color.Red else Color.Black
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}
