package com.beyhivealert.android.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.beyhivealert.android.screens.HomeScreen
import com.beyhivealert.android.screens.VideosScreen
import com.beyhivealert.android.screens.TrackersScreen
import com.beyhivealert.android.screens.ScheduleScreen
import com.beyhivealert.android.screens.NotificationsScreen
import com.beyhivealert.android.screens.SurvivorGameScreen
import com.beyhivealert.android.screens.DailyTriviaScreen
import com.beyhivealert.android.screens.AlbumRankingScreen
import com.beyhivealert.android.components.BottomNavigationBar

@Composable
fun BeyhiveNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MainScreen(navController = navController)
        }
        composable("survivor") {
            SurvivorGameScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable("daily_trivia") {
            DailyTriviaScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable("album_ranker") {
            AlbumRankingScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) } // Start with Home tab selected
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToSurvivor = { navController.navigate("survivor") },
                    onNavigateToDailyTrivia = { navController.navigate("daily_trivia") },
                    onNavigateToAlbumRanker = { navController.navigate("album_ranker") }
                )
                1 -> VideosScreen()
                2 -> NotificationsScreen() // MIDDLE button = Notifications
                3 -> TrackersScreen() // Button to the RIGHT of middle = Outfit/Song tracker
                4 -> ScheduleScreen()
            }
        }
    }
} 