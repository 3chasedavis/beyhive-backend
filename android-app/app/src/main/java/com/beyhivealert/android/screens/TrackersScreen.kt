package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyhivealert.android.viewmodels.OutfitsViewModel
import com.beyhivealert.android.data.Outfit
import coil.compose.AsyncImage
import androidx.compose.ui.geometry.Offset

enum class TrackerTab { SETLIST, OUTFIT }

@Composable
fun TrackersScreen() {
    var selectedTab by remember { mutableStateOf(TrackerTab.SETLIST) }
    
    val acts = listOf(
        "Act 1 - Intro" to listOf(
            "Intro (contains elements of 'AMERIICAN REQUIEM')",
            "AMERIICAN REQUIEM",
            "Blackbird (The Beatles cover) (\"COWBOY CARTER\" version)",
            "The Star-Spangled Banner (John Stafford Smith & Francis Scott Key cover) (includes elements of Jimi Hendrix's instrumental arrangement originally performed at Woodstock)",
            "Freedom (shortened)",
            "YA YA / Why Don't You Love Me",
            "Song played from tape: OH LOUISIANA"
        ),
        "Act 2 - Revolution" to listOf(
            "PROPAGANDA (contains elements of Those Guys' 'An American Poem' and Death Grips' 'You Might Think He Loves…')",
            "AMERICA HAS A PROBLEM (contains elements of 'AMERICA HAS A PROBLEM (feat. Kendrick Lamar)' & 'SPAGHETTII')",
            "SPAGHETTII (contains elements of 'ESSA TÁ QUENTE', 'WTHELLY', 'Flawless', 'Run the World (Girls)' & 'MY POWER')",
            "Formation (shortened)",
            "MY HOUSE (contains elements of Wisp's 'Your Face' and 'Bow Down')",
            "Diva"
        ),
        "Act 3 - Refuge TRAILER" to listOf(
            "TRAILER (contains elements of Justice's 'Genesis', JPEGMAFIA's 'don't rely on other men' and 'I Been On')",
            "ALLIIGATOR TEARS (shortened)",
            "JUST FOR FUN (shortened)",
            "PROTECTOR (with Rumi Carter) (contains elements of 'Dangerously In Love 2')",
            "Song played from tape: The First Time Ever I Saw Your Face (Ewan MacColl & Peggy Seeger song) (Roberta Flack version)",
            "FLAMENCO"
        ),
        "Act 4 - Marfa" to listOf(
            "PEEP SHOW (contains elements of Marian Anderson's 'Deep River', Nancy Sinatra's 'Lightning's Girl')",
            "DESERT EAGLE (extended intro)",
            "RIIVERDANCE (shortened)",
            "II HANDS II HEAVEN (shortened)",
            "TYRANT (shortened; contains elements of 'Haunted')",
            "THIQUE (shortened; contains elements of 'TYRANT', 'Bills, Bills, Bills' & 'Say My Name')",
            "LEVII'S JEANS (shortened; contains elements of 'THIQUE')",
            "SWEET ★ HONEY ★ BUCKIIN' / PURE/HONEY / SUMMER RENAISSANCE (contains elements of 69 Boyz' 'Tootsie Roll')"
        ),
        "Act 5 - Tease" to listOf(
            "OUTLAW (50FT COWBOY) (contains elements of BigXthaPlug's 'The Largest', Esther Marrow's 'Walk Tall' & '7/11')",
            "TEXAS HOLD 'EM (extended intro; contains elements of 'TEXAS HOLD 'EM (PONY UP REMIX)' & 'CHURCH GIRL')",
            "Crazy in Love (Homecoming version; contains elements of Cassidy's 'I'm a Hustla')",
            "Single Ladies (Put a Ring on It) (shortened; contains elements of 'Get Me Bodied')",
            "Love on Top (shortened; contains elements of 'Freakum Dress')",
            "Irreplaceable (shortened)",
            "If I Were a Boy (shortened; contains elements of 'JOLENE')",
            "DOLLY P",
            "Jolene (Dolly Parton cover) (COWBOY CARTER version; contains elements of 'Daddy Lessons')",
            "Daddy Lessons (shortened)",
            "BODYGUARD",
            "II MOST WANTED (snippet; contains elements of 'Blow')",
            "Dance for You / SMOKE HOUR II (contains elements of 'CUFF IT (WETTER REMIX)')",
            "HEATED (shortened; contains elements of 803Fresh's 'Boots on the Ground')",
            "Before I Let Go (Maze cover)"
        ),
        "Act 6 - Renaissance" to listOf(
            "HOLY DAUGHTER (contains elements of 'Ghost' & 'I Care')",
            "DAUGHTER (extended outro)",
            "OPERA (contains elements of 'ENERGY' & 'An American Poem')",
            "I'M THAT GIRL (shortened; contains elements of 'APESHIT')",
            "COZY",
            "ALIEN SUPERSTAR (Shortened)",
            "Song played from tape: Déjà Vu (with Blue Ivy Carter) (dance Interlude)"
        ),
        "Act 7 - Reclaimation" to listOf(
            "LEGACY (contains elements of Michael Jackson's 'I Wanna Be Where You Are' & Those Guys' 'An American Poem')",
            "AMEN (extended intro & outro)"
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Tab Selection Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Song Tracker Button
            Button(
                onClick = { selectedTab = TrackerTab.SETLIST },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == TrackerTab.SETLIST) Color(0xFFEADFA7) else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Song Tracker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == TrackerTab.SETLIST) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            
            // Outfit Tracker Button
            Button(
                onClick = { selectedTab = TrackerTab.OUTFIT },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == TrackerTab.OUTFIT) Color(0xFFEADFA7) else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Outfit Tracker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == TrackerTab.OUTFIT) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
        
        // Main Content
        if (selectedTab == TrackerTab.SETLIST) {
            // Song Tracker Content
            Column {
                Text(
                    text = "Trackers",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Track songs and outfits from each show.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Text(
                    text = "Setlist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Setlist Cards
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(acts.indices.toList()) { idx ->
                        val act = acts[idx]
                        SetlistCard(act = act)
                    }
                }
            }
        } else {
            // Outfit Tracker Content
            OutfitsView()
        }
    }
}

@Composable
fun SetlistCard(act: Pair<String, List<String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEADFA7)), // Same yellow as header
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = act.first,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Black text for contrast on yellow
                modifier = Modifier.padding(bottom = 12.dp)
            )
            act.second.forEach { song ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Music note",
                        tint = Color.Black, // Black icon for contrast on yellow
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = song,
                        fontSize = 14.sp,
                        color = Color.Black // Black text for contrast on yellow
                    )
                }
            }
        }
    }
}

@Composable
fun OutfitsView() {
    val viewModel: OutfitsViewModel = viewModel()
    val outfits by viewModel.outfits
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    LaunchedEffect(Unit) {
        viewModel.fetchOutfits()
    }
    
    Column {
        Text(
            text = "Outfits",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else if (outfits.isEmpty()) {
            Text(
                text = "No outfits available",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(outfits) { outfit ->
                    OutfitCard(outfit = outfit)
                }
            }
        }
    }
}

@Composable
fun OutfitCard(outfit: Outfit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (outfit.imageUrl != null) {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Outfit placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = outfit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (outfit.description != null) {
                    Text(
                        text = outfit.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
} 