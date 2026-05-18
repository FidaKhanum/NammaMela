package com.example.nammamela.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

data class CastMember(val name: String, val role: String)

@Composable
fun CastScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    var castList by remember { mutableStateOf(listOf<CastMember>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("cast").orderBy("order").get()
            .addOnSuccessListener { snap ->
                castList = snap.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val role = doc.getString("role") ?: return@mapNotNull null
                    CastMember(name, role)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2C0A0A), Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "🎭 Tonight's Cast",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107)
            )

            Spacer(modifier = Modifier.height(25.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFC107))
            } else if (castList.isEmpty()) {
                Text(
                    text = "No cast announced yet.",
                    color = Color.White,
                    fontSize = 16.sp
                )
            } else {
                castList.forEach { member ->
                    CastCard(member.name, member.role)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CastCard(name: String, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = role, fontSize = 18.sp)
        }
    }
}