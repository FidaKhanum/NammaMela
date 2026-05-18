//package com.example.nammamela.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//import java.util.concurrent.TimeUnit
//
//data class ChatMessage(
//    val text: String,
//    val isUser: Boolean
//)
//
//private const val API_KEY = "" // <-- paste your HuggingFace token here
//private const val API_URL =
//    "https://openrouter.ai/api/v1/chat/completions"
//
//private val httpClient = OkHttpClient.Builder()
//    .connectTimeout(60, TimeUnit.SECONDS)
//    .readTimeout(60, TimeUnit.SECONDS)
//    .build()
//
//private val freeModels = listOf(
//    "openrouter/free",
//    "meta-llama/llama-3.3-70b-instruct:free",
//    "google/gemma-3-27b-it:free",
//    "mistralai/mistral-small-3.1-24b-instruct:free"
//)
//
//suspend fun askBot(userMessage: String): String {
//    return withContext(Dispatchers.IO) {
//        for (model in freeModels) {
//            try {
//                val jsonBody = JSONObject().apply {
//                    put("model", model)
//                    put("messages", JSONArray().apply {
//                        put(JSONObject().apply {
//                            put("role", "system")
//                            put("content", "You are NammaMela Bot, a helpful assistant for the NammaMela cultural event app. Help users with event schedules, seat bookings, cast info, and fan wall. Keep answers short and friendly.")
//                        })
//                        put(JSONObject().apply {
//                            put("role", "user")
//                            put("content", userMessage)
//                        })
//                    })
//                }
//
//                val requestBody = jsonBody.toString()
//                    .toRequestBody("application/json".toMediaType())
//
//                val request = Request.Builder()
//                    .url(API_URL)
//                    .post(requestBody)
//                    .addHeader("Authorization", "Bearer $API_KEY")
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("HTTP-Referer", "com.example.nammamela")
//                    .build()
//
//                val response = httpClient.newCall(request).execute()
//                val responseBody = response.body?.string() ?: continue
//
//                if (response.code == 429) continue // try next model
//
//                if (!response.isSuccessful) {
//                    return@withContext "API Error ${response.code}: $responseBody"
//                }
//
//                val json = JSONObject(responseBody)
//                return@withContext json.getJSONArray("choices")
//                    .getJSONObject(0)
//                    .getJSONObject("message")
//                    .getString("content")
//                    .trim()
//
//            } catch (e: Exception) {
//                continue // try next model
//            }
//        }
//        "Sorry, all models are busy right now. Please try again in a moment."
//    }
//}
//
//@Composable
//fun ChatBotScreen() {
//    val messages = remember {
//        mutableStateListOf(
//            ChatMessage(
//                "Hello 👋 I'm NammaMela Bot! Ask me anything about the event.",
//                false
//            )
//        )
//    }
//    var question by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//    val scope = rememberCoroutineScope()
//    val listState = rememberLazyListState()
//
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            listState.animateScrollToItem(messages.size - 1)
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "🤖 NammaMela Bot",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 12.dp)
//        )
//
//        HorizontalDivider()
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyColumn(
//            state = listState,
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(messages) { message ->
//                ChatBubble(message)
//            }
//            if (isLoading) {
//                item {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Start
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(Color(0xFFE0E0E0))
//                                .padding(12.dp)
//                        ) {
//                            Text(
//                                "Thinking...",
//                                fontSize = 14.sp,
//                                color = Color.DarkGray
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//        HorizontalDivider()
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            OutlinedTextField(
//                value = question,
//                onValueChange = { question = it },
//                placeholder = { Text("Ask something...") },
//                modifier = Modifier.weight(1f),
//                shape = RoundedCornerShape(24.dp),
//                maxLines = 3
//            )
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            IconButton(
//                onClick = {
//                    val userText = question.trim()
//                    if (userText.isBlank() || isLoading) return@IconButton
//                    messages.add(ChatMessage(userText, true))
//                    question = ""
//                    isLoading = true
//                    scope.launch {
//                        val reply = askBot(userText) // <-- fixed: was askGemini
//                        messages.add(ChatMessage(reply, false))
//                        isLoading = false
//                    }
//                },
//                modifier = Modifier
//                    .size(52.dp)
//                    .clip(RoundedCornerShape(50))
//                    .background(MaterialTheme.colorScheme.primary)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Send,
//                    contentDescription = "Send",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatBubble(message: ChatMessage) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .widthIn(max = 280.dp)
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (message.isUser) 16.dp else 4.dp,
//                        bottomEnd = if (message.isUser) 4.dp else 16.dp
//                    )
//                )
//                .background(
//                    if (message.isUser) MaterialTheme.colorScheme.primary
//                    else Color(0xFFE8E8E8)
//                )
//                .padding(horizontal = 14.dp, vertical = 10.dp)
//        ) {
//            Text(
//                text = message.text,
//                fontSize = 15.sp,
//                color = if (message.isUser) Color.White else Color.Black
//            )
//        }
//    }
//}
//package com.example.nammamela.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//import java.util.concurrent.TimeUnit
//
//data class ChatMessage(
//    val text: String,
//    val isUser: Boolean
//)
//
//private const val API_KEY = ""
//private const val API_URL = "https://openrouter.ai/api/v1/chat/completions"
//
//private val freeModels = listOf(
//    "openrouter/free",
//    "meta-llama/llama-3.3-70b-instruct:free",
//    "google/gemma-3-27b-it:free",
//    "mistralai/mistral-small-3.1-24b-instruct:free"
//)
//
//private val httpClient = OkHttpClient.Builder()
//    .connectTimeout(60, TimeUnit.SECONDS)
//    .readTimeout(60, TimeUnit.SECONDS)
//    .build()
//
//// Fetch real data from Firestore
//suspend fun fetchAppContext(): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            val db = FirebaseFirestore.getInstance()
//
//            // Fetch today's show
//            val showDoc = db.collection("show").document("today").get().await()
//            val showName = showDoc.getString("name") ?: "Unknown"
//            val showTime = showDoc.getString("time") ?: "Unknown"
//            val showVenue = showDoc.getString("venue") ?: "Unknown"
//
//            // Fetch seats
//            val seatsSnap = db.collection("seats").get().await()
//            val totalSeats = seatsSnap.size()
//            val bookedSeats = seatsSnap.documents.count {
//                it.getBoolean("isBooked") == true
//            }
//            val availableSeats = totalSeats - bookedSeats
//
//            // Fetch comments/fan wall
//            val commentsSnap = db.collection("comments").get().await()
//            val recentComments = commentsSnap.documents
//                .take(3)
//                .mapNotNull { it.getString("text") ?: it.getString("comment") ?: it.getString("message") }
//                .joinToString(", ")
//
//            """
//            NammaMela App live data:
//            - Tonight's Show: $showName
//            - Show Time: $showTime
//            - Venue: $showVenue
//            - Total Seats: $totalSeats
//            - Booked Seats: $bookedSeats
//            - Available Seats: $availableSeats
//            - Recent Fan Wall Comments: $recentComments
//            """.trimIndent()
//
//        } catch (e: Exception) {
//            "NammaMela is a cultural event app with shows, seat booking, and a fan wall."
//        }
//    }
//}
//
//suspend fun askBot(userMessage: String, appContext: String): String {
//    return withContext(Dispatchers.IO) {
//        for (model in freeModels) {
//            try {
//                val systemPrompt = """
//                    You are NammaMela Bot, a helpful assistant for the NammaMela cultural event app.
//                    Here is the current live data from the app:
//
//                    $appContext
//
//                    Use this data to answer user questions accurately. Keep answers short and friendly.
//                    If asked about booking, direct them to use the 'Book Tickets' button in the app.
//                """.trimIndent()
//
//                val jsonBody = JSONObject().apply {
//                    put("model", model)
//                    put("messages", JSONArray().apply {
//                        put(JSONObject().apply {
//                            put("role", "system")
//                            put("content", systemPrompt)
//                        })
//                        put(JSONObject().apply {
//                            put("role", "user")
//                            put("content", userMessage)
//                        })
//                    })
//                }
//
//                val requestBody = jsonBody.toString()
//                    .toRequestBody("application/json".toMediaType())
//
//                val request = Request.Builder()
//                    .url(API_URL)
//                    .post(requestBody)
//                    .addHeader("Authorization", "Bearer $API_KEY")
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("HTTP-Referer", "com.example.nammamela")
//                    .build()
//
//                val response = httpClient.newCall(request).execute()
//                val responseBody = response.body?.string() ?: continue
//
//                if (response.code == 429 || response.code == 404) continue
//
//                if (!response.isSuccessful) {
//                    return@withContext "API Error ${response.code}: $responseBody"
//                }
//
//                val json = JSONObject(responseBody)
//                return@withContext json.getJSONArray("choices")
//                    .getJSONObject(0)
//                    .getJSONObject("message")
//                    .getString("content")
//                    .trim()
//
//            } catch (e: Exception) {
//                continue
//            }
//        }
//        "Sorry, all models are busy right now. Please try again in a moment."
//    }
//}
//
//@Composable
//fun ChatBotScreen() {
//    val messages = remember {
//        mutableStateListOf(
//            ChatMessage(
//                "Hello 👋 I'm NammaMela Bot! Ask me about tonight's show, seats, or anything else!",
//                false
//            )
//        )
//    }
//    var question by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//    var appContext by remember { mutableStateOf("") }
//    val scope = rememberCoroutineScope()
//    val listState = rememberLazyListState()
//
//    // Fetch Firestore data when screen opens
//    LaunchedEffect(Unit) {
//        appContext = fetchAppContext()
//    }
//
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            listState.animateScrollToItem(messages.size - 1)
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "🤖 NammaMela Bot",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 12.dp)
//        )
//
//        HorizontalDivider()
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyColumn(
//            state = listState,
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(messages) { message ->
//                ChatBubble(message)
//            }
//            if (isLoading) {
//                item {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Start
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(Color(0xFFE0E0E0))
//                                .padding(12.dp)
//                        ) {
//                            Text("Thinking...", fontSize = 14.sp, color = Color.DarkGray)
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//        HorizontalDivider()
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            OutlinedTextField(
//                value = question,
//                onValueChange = { question = it },
//                placeholder = { Text("Ask something...") },
//                modifier = Modifier.weight(1f),
//                shape = RoundedCornerShape(24.dp),
//                maxLines = 3
//            )
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            IconButton(
//                onClick = {
//                    val userText = question.trim()
//                    if (userText.isBlank() || isLoading) return@IconButton
//                    messages.add(ChatMessage(userText, true))
//                    question = ""
//                    isLoading = true
//                    scope.launch {
//                        val reply = askBot(userText, appContext)
//                        messages.add(ChatMessage(reply, false))
//                        isLoading = false
//                    }
//                },
//                modifier = Modifier
//                    .size(52.dp)
//                    .clip(RoundedCornerShape(50))
//                    .background(MaterialTheme.colorScheme.primary)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Send,
//                    contentDescription = "Send",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatBubble(message: ChatMessage) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .widthIn(max = 280.dp)
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (message.isUser) 16.dp else 4.dp,
//                        bottomEnd = if (message.isUser) 4.dp else 16.dp
//                    )
//                )
//                .background(
//                    if (message.isUser) MaterialTheme.colorScheme.primary
//                    else Color(0xFFE8E8E8)
//                )
//                .padding(horizontal = 14.dp, vertical = 10.dp)
//        ) {
//            Text(
//                text = message.text,
//                fontSize = 15.sp,
//                color = if (message.isUser) Color.White else Color.Black
//            )
//        }
//    }
//}
package com.example.nammamela.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammamela.navigation.Routes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// Message types
sealed class BotMessage {
    data class Text(val text: String, val isUser: Boolean) : BotMessage()
    data class BookingButton(val label: String) : BotMessage()
}

private const val API_KEY = "API_KEY"
private const val API_URL = "https://openrouter.ai/api/v1/chat/completions"

private val freeModels = listOf(
    "openrouter/free",
    "meta-llama/llama-3.3-70b-instruct:free",
    "google/gemma-3-27b-it:free",
    "mistralai/mistral-small-3.1-24b-instruct:free"
)

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

suspend fun fetchAppContext(): String {
    return withContext(Dispatchers.IO) {
        try {
            val db = FirebaseFirestore.getInstance()

            // Fetch today's show
            val showDoc = db.collection("show").document("today").get().await()
            val showName = showDoc.getString("name") ?: "Unknown"
            val showTime = showDoc.getString("time") ?: "Unknown"
            val showVenue = showDoc.getString("venue") ?: "Unknown"

            // Fetch seats
            val seatsSnap = db.collection("seats").get().await()
            val totalSeats = seatsSnap.size()
            val bookedSeats = seatsSnap.documents.count {
                it.getBoolean("booked") == true
            }
            val availableSeats = totalSeats - bookedSeats
            val availableSeatIds = seatsSnap.documents
                .filter { it.getBoolean("booked") != true }
                .mapNotNull { it.id }
                .joinToString(", ")
            val bookedSeatIds = seatsSnap.documents
                .filter { it.getBoolean("booked") == true }
                .mapNotNull { it.id }
                .joinToString(", ")

            // Fetch cast
            val castSnap = db.collection("cast").get().await()
            val castInfo = castSnap.documents.joinToString("\n") { doc ->
                "  - ${doc.getString("name")} as ${doc.getString("role")}"
            }.ifEmpty { "No cast announced yet" }

            // Fetch show history
            val historySnap = db.collection("showHistory").get().await()
            val pastShows = historySnap.documents
                .sortedByDescending { it.getString("date") }
                .take(5)
                .joinToString("\n") { doc ->
                    "  - ${doc.getString("name")} on ${doc.getString("date")} " +
                            "at ${doc.getString("time")} in ${doc.getString("venue")}"
                }
                .ifEmpty { "No past shows recorded yet" }

            // Fetch recent fan wall comments
            val commentsSnap = db.collection("comments").get().await()
            val recentComments = commentsSnap.documents
                .take(3)
                .mapNotNull {
                    it.getString("text")
                        ?: it.getString("comment")
                        ?: it.getString("message")
                }
                .joinToString(", ")
                .ifEmpty { "No comments yet" }

            """
            NammaMela App live data:

            TONIGHT'S SHOW:
            - Show Name: $showName
            - Time: $showTime
            - Venue: $showVenue

            CAST:
            $castInfo

            SEATS:
            - Total Seats: $totalSeats
            - Booked Seats: $bookedSeats (${bookedSeatIds.ifEmpty { "none" }})
            - Available Seats: $availableSeats (${availableSeatIds.ifEmpty { "none" }})

            PAST SHOWS:
            $pastShows

            FAN WALL:
            - Recent Comments: $recentComments
            """.trimIndent()

        } catch (e: Exception) {
            "NammaMela is a cultural event app with shows, seat booking, and a fan wall."
        }
    }
}

// Returns Pair<responseText, showBookingButton>
suspend fun askBot(userMessage: String, appContext: String): Pair<String, Boolean> {
    return withContext(Dispatchers.IO) {
        for (model in freeModels) {
            try {
                val systemPrompt = """
                    You are NammaMela Bot, a helpful assistant for the NammaMela cultural event app.
                    Here is the current live data from the app:
                    
                    $appContext
                    
                    Use this data to answer user questions accurately. Keep answers short and friendly.
                    
                    IMPORTANT RULES:
1. Only add [SHOW_BOOKING_BUTTON] on a new line when user EXPLICITLY says they want to book tickets with words like "book ticket", "reserve seat", "buy ticket", "I want to book". Do NOT add it for questions about cast, show info, or availability.
2. For cast questions, answer using ONLY the CAST data provided above. Do not say cast info is unavailable if it is listed above.
3. Never suggest checking external sources - all info is in the data provided.
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("model", model)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        })
                    })
                }

                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "com.example.nammamela")
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: continue

                if (response.code == 429 || response.code == 404) continue
                if (!response.isSuccessful) {
                    return@withContext Pair("API Error ${response.code}: $responseBody", false)
                }

                val json = JSONObject(responseBody)
                val fullText = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                // Check if bot wants to show booking button
                val showButton = fullText.contains("[SHOW_BOOKING_BUTTON]")
                val cleanText = fullText.replace("[SHOW_BOOKING_BUTTON]", "").trim()

                return@withContext Pair(cleanText, showButton)

            } catch (e: Exception) {
                continue
            }
        }
        Pair("Sorry, all models are busy right now. Please try again in a moment.", false)
    }
}

@Composable
fun ChatBotScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    val messages = remember { mutableStateListOf<BotMessage>() }
    var question by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var appContext by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Load chat history from Firestore on open
    LaunchedEffect(Unit) {
        //appContext = fetchAppContext()

        db.collection("chatHistory")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                messages.clear()
                if (snapshot.isEmpty) {
                    messages.add(
                        BotMessage.Text(
                            "Hello 👋 I'm NammaMela Bot! Ask me about tonight's show, seats, or anything else!",
                            false
                        )
                    )
                } else {
                    snapshot.documents.forEach { doc ->
                        when (doc.getString("type")) {
                            "text" -> messages.add(
                                BotMessage.Text(
                                    doc.getString("text") ?: "",
                                    doc.getBoolean("isUser") ?: false
                                )
                            )
                            "button" -> messages.add(
                                BotMessage.BookingButton(
                                    doc.getString("label") ?: "Book Tickets Now"
                                )
                            )
                        }
                    }
                }
            }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with Clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🤖 NammaMela Bot",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = {
                db.collection("chatHistory").get()
                    .addOnSuccessListener { snapshot ->
                        snapshot.documents.forEach { it.reference.delete() }
                        messages.clear()
                        messages.add(
                            BotMessage.Text(
                                "Hello 👋 I'm NammaMela Bot! Ask me about tonight's show, seats, or anything else!",
                                false
                            )
                        )
                    }
            }) {
                Text("Clear", color = Color.Gray)
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                when (message) {
                    is BotMessage.Text -> ChatBubble(message.text, message.isUser)
                    is BotMessage.BookingButton -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(Routes.Seat.route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "🎟️ ${message.label}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0E0E0))
                                .padding(12.dp)
                        ) {
                            Text("Thinking...", fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Ask something...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val userText = question.trim()
                    if (userText.isBlank() || isLoading) return@IconButton
                    messages.add(BotMessage.Text(userText, true))
                    question = ""
                    isLoading = true

                    // Save user message to Firestore
                    db.collection("chatHistory").add(
                        mapOf(
                            "type" to "text",
                            "text" to userText,
                            "isUser" to true,
                            "timestamp" to System.currentTimeMillis()
                        )
                    )

                    scope.launch {
                        appContext = fetchAppContext() // refresh before every message
                        val (reply, showButton) = askBot(userText, appContext)
                        messages.add(BotMessage.Text(reply, false))

                        // Save bot reply to Firestore
                        db.collection("chatHistory").add(
                            mapOf(
                                "type" to "text",
                                "text" to reply,
                                "isUser" to false,
                                "timestamp" to System.currentTimeMillis()
                            )
                        )

                        if (showButton) {
                            messages.add(BotMessage.BookingButton("Book Tickets Now"))
                            db.collection("chatHistory").add(
                                mapOf(
                                    "type" to "button",
                                    "label" to "Book Tickets Now",
                                    "isUser" to false,
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary
                    else Color(0xFFE8E8E8)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}