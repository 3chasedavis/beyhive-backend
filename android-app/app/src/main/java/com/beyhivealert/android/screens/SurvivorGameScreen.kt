package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SurvivorQuiz(
    val title: String,
    val id: String
)

@Composable
fun SurvivorGameScreen(
    onBackPressed: () -> Unit
) {
    BackHandler {
        onBackPressed()
    }
    var showQuiz by remember { mutableStateOf(false) }
    var selectedQuiz by remember { mutableStateOf<SurvivorQuiz?>(null) }
    
    val quizzes = remember {
        listOf(
            SurvivorQuiz("Las Vegas Night 1", "vegas_n1"),
            SurvivorQuiz("Las Vegas Night 2", "vegas_n2")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
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
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Survivor Game",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Title
            Text(
                text = "SURVIVOR GAME",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
            )

            // Quiz List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(quizzes) { quiz ->
                    Button(
                        onClick = {
                            selectedQuiz = quiz
                            showQuiz = true
                        },
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
                                        colors = listOf(Color.Red.copy(alpha = 0.7f), Color.White, Color.Blue.copy(alpha = 0.7f))
                                    ),
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = quiz.title,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Quiz",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Open Quiz",
                                    tint = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // User note
            Text(
                text = "If one of the quizzes doesn't load for you on the first try, go out of it, click on a different quiz, and then go back into the quiz you originally wanted. It should work!",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
            )
        }

        // Quiz sheet
        if (showQuiz && selectedQuiz != null) {
            SurvivorQuizSheet(
                quiz = selectedQuiz!!,
                onDismiss = { showQuiz = false }
            )
        }
    }
}

@Composable
fun SurvivorQuizSheet(
    quiz: SurvivorQuiz,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var submittedAnswers by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var quizOpen by remember { mutableStateOf(true) }
    var quizOpenMessage by remember { mutableStateOf<String?>(null) }
    var questions by remember { mutableStateOf(listOf<QuizQuestion>()) }
    var answers by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var showDropdown by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    var showIncompleteAlert by remember { mutableStateOf(false) }

    // Mock questions for demo
    val mockQuestions = remember {
        listOf(
            QuizQuestion(
                index = 0,
                text = "What outfit did Beyoncé wear during the opening?",
                points = 10,
                options = listOf("Red bodysuit", "Blue jumpsuit", "White dress", "Black leather"),
                icon = null
            ),
            QuizQuestion(
                index = 1,
                text = "What was the first song performed?",
                points = 15,
                options = listOf("Formation", "Crazy in Love", "Break My Soul", "Texas Hold 'Em"),
                icon = null
            ),
            QuizQuestion(
                index = 2,
                text = "How many outfit changes were there?",
                points = 5,
                options = listOf("3", "5", "7", "9"),
                icon = null
            )
        )
    }

    LaunchedEffect(Unit) {
        // Simulate loading
        kotlinx.coroutines.delay(1000)
        questions = mockQuestions
        isLoading = false
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
        // Back button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(12.dp)
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else if (quizOpenMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = quizOpenMessage!!,
                    fontSize = 20.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        } else if (hasSubmitted) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = quiz.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                    Text(
                        text = "Your Answers",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                items(questions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = question.text,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                text = submittedAnswers[question.index] ?: "No answer",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                item {
                    Text(
                        text = "Thank you for playing! Your answers have been submitted. Good luck!",
                        color = Color.Green,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = quiz.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                    Text(
                        text = "Questions",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                items(questions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Question ${question.index + 1}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${question.points} pts",
                                fontSize = 12.sp,
                                color = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .background(Color.Red.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            Text(
                                text = "Required",
                                fontSize = 10.sp,
                                color = Color.Red.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.text,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Simple dropdown simulation
                            question.options?.forEach { option ->
                                Button(
                                    onClick = {
                                        answers[question.index] = option
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (answers[question.index] == option) 
                                            Color.Blue.copy(alpha = 0.2f) 
                                        else 
                                            Color.Gray.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = option,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = {
                            val allAnswered = questions.indices.all { answers[it]?.isNotEmpty() == true }
                            if (allAnswered) {
                                submittedAnswers = answers.toMutableMap()
                                hasSubmitted = true
                            } else {
                                showIncompleteAlert = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Submit",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }


    }

    // Alert dialog for incomplete answers
    if (showIncompleteAlert) {
        AlertDialog(
            onDismissRequest = { showIncompleteAlert = false },
            title = { Text("Incomplete") },
            text = { Text("Please answer all questions before submitting.") },
            confirmButton = {
                TextButton(onClick = { showIncompleteAlert = false }) {
                    Text("OK")
                }
            }
        )
    }
}

data class QuizQuestion(
    val index: Int,
    val text: String,
    val points: Int,
    val options: List<String>,
    val icon: String?
)
