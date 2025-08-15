package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val coverImageName: String
)

data class AlbumRanking(
    val id: String,
    val nickname: String,
    val ranking: List<Album>,
    val likes: List<String>,
    val createdAt: String,
    val userId: String
) {
    val likesCount: Int get() = likes.size
}

@Composable
fun AlbumRankingScreen(
    onBackPressed: () -> Unit
) {
    BackHandler {
        onBackPressed()
    }
    var showRankAlbums by remember { mutableStateOf(false) }
    var showCommunityWall by remember { mutableStateOf(false) }
    var showLikes by remember { mutableStateOf(false) }
    
    val albums = remember {
        listOf(
            Album("1", "DANGEROUSLY IN LOVE", "Beyoncé", "dangerous_in_love"),
            Album("2", "B'DAY", "Beyoncé", "bday"),
            Album("3", "I AM... SASHA FIERCE", "Beyoncé", "sasha_fierce"),
            Album("4", "4", "Beyoncé", "four"),
            Album("5", "Self-Titled", "Beyoncé", "beyonce"),
            Album("6", "LEMONADE", "Beyoncé", "lemonade"),
            Album("7", "RENAISSANCE", "Beyoncé", "renaissance"),
            Album("8", "COWBOY CARTER", "Beyoncé", "cowboy_carter")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Red.copy(alpha = 0.7f), Color.White, Color.Blue.copy(alpha = 0.7f))
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Album Ranker",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Description
            Text(
                text = "Album Ranker is a space to share your personal ranking of the albums and see how the community feels. Drag to rank, post your list, and like your favorites!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Main buttons
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(
                    onClick = { showRankAlbums = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Red.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.18f),
                                        Color.Blue.copy(alpha = 0.18f)
                                    )
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Rank Albums",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "drag and drop to create your album ranking",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Button(
                    onClick = { showCommunityWall = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Red.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.18f),
                                        Color.Blue.copy(alpha = 0.18f)
                                    )
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Read Community Ranks",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "see how the community ranks the albums",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Button(
                    onClick = { showLikes = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Red.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.18f),
                                        Color.Blue.copy(alpha = 0.18f)
                                    )
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Likes",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "see all the rankings you have liked",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Sheets
        if (showRankAlbums) {
            AlbumRankingCreationSheet(
                albums = albums,
                onDismiss = { showRankAlbums = false }
            )
        }

        if (showCommunityWall) {
            CommunityWallSheet(
                onDismiss = { showCommunityWall = false }
            )
        }

        if (showLikes) {
            LikesSheet(
                onDismiss = { showLikes = false }
            )
        }
    }
}

@Composable
fun AlbumRankingCreationSheet(
    albums: List<Album>,
    onDismiss: () -> Unit
) {
    var rankedAlbums by remember { mutableStateOf(albums) }
    var nickname by remember { mutableStateOf(TextFieldValue("")) }
    var showSuccess by remember { mutableStateOf(false) }
    
    val columns = listOf("Best", "Great", "Good", "Okay", "Meh", "Worst")
    var columnAlbums by remember { mutableStateOf(mutableMapOf<String, List<Album>>()) }
    
    LaunchedEffect(Unit) {
        val initial = mutableMapOf<String, List<Album>>()
        columns.forEach { col -> initial[col] = emptyList() }
        initial["Best"] = albums
        columnAlbums = initial
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.Red.copy(alpha = 0.7f), Color.White, Color.Blue.copy(alpha = 0.7f))
                )
            )
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Blue
                    )
                    Text(
                        text = "Back",
                        color = Color.Blue,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Columns
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(columns) { col ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text(
                            text = col,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(columnAlbums[col] ?: emptyList()) { album ->
                                Text(
                                    text = album.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            if (nickname.text.isEmpty()) {
                                Text(
                                    text = "Enter your nickname",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        showSuccess = true
                        // Reset after 2 seconds
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            kotlinx.coroutines.delay(2000)
                            showSuccess = false
                        }
                    },
                    enabled = nickname.text.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "Post to Community Wall",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                if (showSuccess) {
                    Text(
                        text = "Success! Your ranking was posted.",
                        color = Color.Green,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityWallSheet(
    onDismiss: () -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val mockRankings = remember {
        listOf(
            AlbumRanking(
                id = "1",
                nickname = "BeyhiveQueen",
                ranking = listOf(
                    Album("6", "LEMONADE", "Beyoncé", "lemonade"),
                    Album("7", "RENAISSANCE", "Beyoncé", "renaissance"),
                    Album("5", "Self-Titled", "Beyoncé", "beyonce")
                ),
                likes = listOf("user1", "user2"),
                createdAt = "2024-01-01",
                userId = "user1"
            ),
            AlbumRanking(
                id = "2",
                nickname = "HiveMember",
                ranking = listOf(
                    Album("7", "RENAISSANCE", "Beyoncé", "renaissance"),
                    Album("8", "COWBOY CARTER", "Beyoncé", "cowboy_carter"),
                    Album("6", "LEMONADE", "Beyoncé", "lemonade")
                ),
                likes = listOf("user1"),
                createdAt = "2024-01-02",
                userId = "user2"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Blue
                    )
                    Text(
                        text = "Back",
                        color = Color.Blue,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Search
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            if (searchText.text.isEmpty()) {
                                Text(
                                    text = "Search by nickname",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            // Rankings list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                val filteredRankings = mockRankings.filter { ranking ->
                    searchText.text.isEmpty() || 
                    ranking.nickname.lowercase().contains(searchText.text.lowercase())
                }
                
                items(filteredRankings) { ranking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = ranking.nickname,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Likes",
                                        tint = Color(0xFFE91E63),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${ranking.likesCount}",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ranking.ranking.forEachIndexed { index, album ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(text = album.title)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LikesSheet(
    onDismiss: () -> Unit
) {
    val mockLikedRankings = remember {
        listOf(
            AlbumRanking(
                id = "1",
                nickname = "BeyhiveQueen",
                ranking = listOf(
                    Album("6", "LEMONADE", "Beyoncé", "lemonade"),
                    Album("7", "RENAISSANCE", "Beyoncé", "renaissance"),
                    Album("5", "Self-Titled", "Beyoncé", "beyonce")
                ),
                likes = listOf("user1", "user2"),
                createdAt = "2024-01-01",
                userId = "user1"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Blue
                    )
                    Text(
                        text = "Back",
                        color = Color.Blue,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Liked rankings list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(mockLikedRankings) { ranking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = ranking.nickname,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Liked",
                                        tint = Color(0xFFE91E63),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${ranking.likesCount}",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ranking.ranking.forEachIndexed { index, album ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(text = album.title)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
