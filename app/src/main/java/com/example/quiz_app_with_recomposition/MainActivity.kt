package com.example.quiz_app_with_recomposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApp()
        }
    }
}

@Composable
fun QuizApp() {
    val questions = listOf(
        Pair("What is the capital of France?", "Paris"),
        Pair("What is 2 + 2?", "4"),
        Pair("What is 1 * 3", "3"),
        Pair("What is 4 / 2", "2")
    )

    var currentIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var remainingChances by remember { mutableStateOf(3) }
    var isQuizComplete by remember { mutableStateOf(false) }
    val incorrectAnswers = remember { mutableStateListOf<Pair<String, String>>() }
    var correctAnswers by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (isQuizComplete) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Quiz Complete!", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Correct answers: $correctAnswers / ${questions.size}")
            Spacer(modifier = Modifier.height(16.dp))

            if (incorrectAnswers.isNotEmpty()) {
                Text(text = "Review Incorrect Answers:")
                incorrectAnswers.forEach { (question, correctAnswer) ->
                    Text(text = "$question\nCorrect Answer: $correctAnswer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                currentIndex = 0
                userInput = ""
                remainingChances = 3
                correctAnswers = 0
                isQuizComplete = false
                incorrectAnswers.clear()
            }) {
                Text("Restart Quiz")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = questions[currentIndex].first,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val correctAnswer = questions[currentIndex].second
                if (userInput.equals(correctAnswer, ignoreCase = true)) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Correct!")
                    }
                    correctAnswers++
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        userInput = ""
                        remainingChances = 3
                    } else {
                        isQuizComplete = true
                    }
                } else {
                    remainingChances--
                    if (remainingChances > 0) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Incorrect! $remainingChances chances left.")
                        }
                    } else {
                        incorrectAnswers.add(questions[currentIndex])
                        scope.launch {
                            snackbarHostState.showSnackbar("Out of chances! Moving to the next question.")
                        }
                        if (currentIndex < questions.size - 1) {
                            currentIndex++
                            userInput = ""
                            remainingChances = 3
                        } else {
                            isQuizComplete = true
                        }
                    }
                }
            }) {
                Text("Submit Answer")
            }

            Spacer(modifier = Modifier.height(16.dp))


            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuizApp()
}
