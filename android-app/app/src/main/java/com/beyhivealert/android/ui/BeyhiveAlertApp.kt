package com.beyhivealert.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beyhivealert.android.navigation.BeyhiveAlertNavigation

@Composable
fun BeyhiveAlertApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BeyhiveAlertNavigation()
    }
} 