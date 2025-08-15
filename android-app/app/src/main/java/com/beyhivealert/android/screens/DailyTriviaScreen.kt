package com.beyhivealert.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler

data class TriviaQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val type: QuestionType,
    val options: List<String>? = null,
    val correctAnswer: String,
    val explanation: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE, TRUE_FALSE, OPEN_ENDED
}

@Composable
fun DailyTriviaScreen(
    onBackPressed: () -> Unit
) {
    BackHandler {
        onBackPressed()
    }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var hasAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showFinal by remember { mutableStateOf(false) }
    var sessionQuestions by remember { mutableStateOf(listOf<TriviaQuestion>()) }
    var openEndedAnswer by remember { mutableStateOf(TextFieldValue("")) }

    val questions = remember {
        listOf(
            TriviaQuestion(
                question = "What is Beyoncé's full name?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf(
                    "Beyoncé Gabrielle Knowles-Carter",
                    "Beyoncé Grace Knowles-Carter",
                    "Beyoncé Georgette Knowles-Carter",
                    "Beyoncé Giselle Knowles-Carter"
                ),
                correctAnswer = "Beyoncé Giselle Knowles-Carter"
            ),
            TriviaQuestion(
                question = "Beyoncé's debut solo album was 'Dangerously in Love.'",
                type = QuestionType.TRUE_FALSE,
                options = listOf("True", "False"),
                correctAnswer = "True"
            ),
            TriviaQuestion(
                question = "What is the name of Beyoncé's all-female tour band?",
                type = QuestionType.OPEN_ENDED,
                correctAnswer = "Suga Mama",
                explanation = "In 2006, Beyoncé introduced her all-female tour band Suga Mama."
            ),
            TriviaQuestion(
                question = "In which year did Beyoncé release her debut solo album, Dangerously in Love?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf("2002", "2003", "2004", "2005"),
                correctAnswer = "2003"
            ),
            TriviaQuestion(
                question = "Beyoncé rose to fame in the late 1990s as the lead singer of what R&B girl-group?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf("The Spice Girls", "Destiny's Child", "TLC", "En Vogue"),
                correctAnswer = "Destiny's Child"
            ),
            TriviaQuestion(
                question = "Who did Beyoncé marry in 2008?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf("Jay-Z", "Ice Cube", "Kanye West", "Sean Combs"),
                correctAnswer = "Jay-Z"
            ),
            TriviaQuestion(
                question = "What is the name of Beyoncé's alter ego?",
                type = QuestionType.MULTIPLE_CHOICE,
                options = listOf("Yoncé", "Sasha Fierce", "Queen Bey", "Mrs. Carter"),
                correctAnswer = "Sasha Fierce"
            ),
            TriviaQuestion(
                question = "True or False: Beyoncé made her big screen debut in Dreamgirls.",
                type = QuestionType.TRUE_FALSE,
                options = listOf("True", "False"),
                correctAnswer = "False",
                explanation = "Her film debut was in Austin Powers in Goldmember."
            )
        )
    }

    fun getSessionQuestions(): List<TriviaQuestion> {
        return questions.shuffled().take(5)
    }
    
    LaunchedEffect(Unit) {
        sessionQuestions = getSessionQuestions()
    }

    fun resetGame() {
        currentIndex = 0
        selectedAnswer = null
        showResult = false
        hasAnswered = false
        score = 0
        showFinal = false
        sessionQuestions = getSessionQuestions()
        openEndedAnswer = TextFieldValue("")
    }

    fun nextQuestion() {
        selectedAnswer = null
        showResult = false
        hasAnswered = false
        openEndedAnswer = TextFieldValue("")
        if (currentIndex + 1 < sessionQuestions.size) {
            currentIndex += 1
        } else {
            showFinal = true
        }
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
            onClick = onBackPressed,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
                            Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        .padding(12.dp)
                )
        }

        if (showFinal) {
            // Final score screen
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Trivia Complete!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Score: $score / ${sessionQuestions.size}",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "Play Again",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        } else if (currentIndex < sessionQuestions.size) {
            val question = sessionQuestions[currentIndex]
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 80.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${sessionQuestions.size}",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = question.question,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            when (question.type) {
                                QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE -> {
                                    question.options?.let { options ->
                                        options.forEach { option ->
                                            Button(
                                                onClick = {
                                                    if (hasAnswered) return@Button
                                                    selectedAnswer = option
                                                    hasAnswered = true
                                                    showResult = true
                                                    if (option.trim().lowercase() == question.correctAnswer.trim().lowercase()) {
                                                        score += 1
                                                    }
                                                    // Auto-advance after a delay
                                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                                        kotlinx.coroutines.delay(1500)
                                                        nextQuestion()
                                                    }
                                                },
                                                enabled = !hasAnswered,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (selectedAnswer == option) 
                                                        Color.Yellow.copy(alpha = 0.5f) 
                                                    else 
                                                        Color.Yellow.copy(alpha = 0.15f)
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp)
                                                ) {
                                                    Text(
                                                        text = option,
                                                        fontSize = 18.sp,
                                                        color = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                QuestionType.OPEN_ENDED -> {
                                    BasicTextField(
                                        value = openEndedAnswer,
                                        onValueChange = { openEndedAnswer = it },
                                        enabled = !hasAnswered,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .padding(16.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        ),
                                        decorationBox = { innerTextField ->
                                            if (openEndedAnswer.text.isEmpty()) {
                                                Text(
                                                    text = "Type your answer...",
                                                    fontSize = 18.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            hasAnswered = true
                                            showResult = true
                                            if (openEndedAnswer.text.trim().lowercase() == question.correctAnswer.trim().lowercase()) {
                                                score += 1
                                            }
                                            // Auto-advance after a delay
                                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                                kotlinx.coroutines.delay(1500)
                                                nextQuestion()
                                            }
                                        },
                                        enabled = !hasAnswered && openEndedAnswer.text.isNotEmpty(),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f))
                                    ) {
                                        Text(
                                            text = "Submit",
                                            fontSize = 18.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }

                            if (showResult) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    if (selectedAnswer?.trim()?.lowercase() == question.correctAnswer.trim().lowercase() ||
                                        openEndedAnswer.text.trim().lowercase() == question.correctAnswer.trim().lowercase()) {
                                        Text(
                                            text = "Correct!",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Green
                                        )
                                    } else {
                                        Text(
                                            text = "Incorrect",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Red.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = "Correct answer: ${question.correctAnswer}",
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                    }
                                    question.explanation?.let { explanation ->
                                        Text(
                                            text = explanation,
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
