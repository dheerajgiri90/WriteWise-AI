
package com.dheeraj.writewiseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dheeraj.writewiseai.data.remote.RetrofitClient
import com.dheeraj.writewiseai.data.remote.RewriteRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                WriteWiseScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteWiseScreen() {

    var aiStatus by remember {
        mutableStateOf("AI status not checked")
    }

    var isChecking by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var selectedAction by remember {
        mutableStateOf("Professional")
    }

    val actions = listOf("Grammar", "Professional", "Shorten")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WriteWise AI ✍️") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Write better with AI",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Enter your message and choose an action."
            )

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Your message") },
                placeholder = {
                    Text("e.g. i will finish this task tomorow")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Text(
                text = "Writing style",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { action ->
                    FilterChip(
                        selected = selectedAction == action,
                        onClick = { selectedAction = action },
                        label = { Text(action) }
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isChecking = true

                        val manager = GeminiNanoManager()
                        aiStatus = manager.checkAvailability()
                        manager.close()

                        isChecking = false
                    }
                },
                enabled = !isChecking,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isChecking) "Checking AI..."
                    else "Check AI Availability"
                )
            }

            Text(
                text = aiStatus,
                style = MaterialTheme.typography.bodyMedium
            )


            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        outputText = ""

                        try {
                            val response = RetrofitClient.api.rewriteText(
                                RewriteRequest(
                                    text = inputText.trim(),
                                    style = selectedAction
                                )
                            )

                            if (response.isSuccessful) {
                                outputText = response.body()?.result
                                    ?: "The AI returned an empty response."
                            } else {
                                outputText =
                                    "Request failed (${response.code()}). " +
                                            "Please try again."
                            }
                        } catch (e: Exception) {
                            outputText =
                                "Could not connect to the AI service. " +
                                        "Check your network and backend server."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Improving...")
                } else {
                    Text("Improve my writing")
                }
            }


            if (outputText.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Result",
                    style = MaterialTheme.typography.titleLarge
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = outputText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
