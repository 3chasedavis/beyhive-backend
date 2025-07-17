package com.beyhivealert.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BeyhiveAlertNavigation() {
    val navController = rememberNavController()
    BeyhiveNavGraph(navController = navController)
} 