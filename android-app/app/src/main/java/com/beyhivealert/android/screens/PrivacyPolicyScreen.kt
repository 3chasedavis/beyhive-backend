package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beyhivealert.android.components.Header

@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
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
                .background(Color.White) // White background for content
        ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Effective Date: July 2025", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Beyhive Alert values your privacy. This policy explains how we handle your information when you use our app and website.")

            Text("Information Collection", fontWeight = FontWeight.Bold)
            Text("- We do not collect personal information such as your name, email, or location.\n- We do not require you to create an account or sign in to use the app.\n- We do not use analytics or advertising SDKs that collect user data.")

            Text("Third-Party Services", fontWeight = FontWeight.Bold)
            Text("Beyhive Alert may display content from third-party sources (such as Instagram, TikTok, or news feeds). We are not responsible for the privacy practices of these external services. Please review their privacy policies for more information.")

            Text("Push Notifications", fontWeight = FontWeight.Bold)
            Text("If you enable notifications, we use standard notification services. No personal data is collected or stored by Beyhive Alert for this purpose.")

            Text("Children's Privacy", fontWeight = FontWeight.Bold)
            Text("Beyhive Alert is not intended for children under 13. We do not knowingly collect information from children under 13.")

            Text("Contact", fontWeight = FontWeight.Bold)
            Text("If you have any questions about this privacy policy, please contact us at support@beyhivealert.com.")

            Text("This policy may be updated from time to time. Please check this page for the latest information.")
            
            // Terms of Service Section
            Spacer(modifier = Modifier.height(20.dp))
            
            Text("Terms of Service", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("By using Beyhive Alert, you agree to the following terms:")
            
            Text("1. Service Description", fontWeight = FontWeight.SemiBold)
            Text("Beyhive Alert is an information aggregation service that provides updates about Beyoncé-related events, news, and content from publicly available sources.")
            
            Text("2. User Responsibilities", fontWeight = FontWeight.SemiBold)
            Text("• You agree to use the app for lawful purposes only\n• You will not attempt to reverse engineer or modify the app\n• You are responsible for maintaining the security of your device")
            
            Text("3. Content and Accuracy", fontWeight = FontWeight.SemiBold)
            Text("While we strive for accuracy, Beyhive Alert aggregates information from third-party sources and cannot guarantee the completeness or accuracy of all information displayed.")
            
            Text("4. Third-Party Services", fontWeight = FontWeight.SemiBold)
            Text("The app may contain links to third-party websites and services. We are not responsible for the content, privacy policies, or practices of these external services.")
            
            Text("5. Limitation of Liability", fontWeight = FontWeight.SemiBold)
            Text("Beyhive Alert is provided 'as is' without warranties of any kind. We are not liable for any damages arising from the use of our service.")
            
            Text("6. Changes to Terms", fontWeight = FontWeight.SemiBold)
            Text("We reserve the right to modify these terms at any time. Continued use of the app constitutes acceptance of updated terms.")
            
            Text("7. Contact Information", fontWeight = FontWeight.SemiBold)
            Text("For questions about these terms, contact us at support@beyhivealert.com")
        }
        }
    }
}


