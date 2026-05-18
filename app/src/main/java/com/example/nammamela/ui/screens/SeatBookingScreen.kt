package com.example.nammamela.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import kotlinx.coroutines.tasks.await

@Composable
fun SeatBookingScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()

    val seats = listOf(
        "A1","A2","A3","A4",
        "B1","B2","B3","B4",
        "C1","C2","C3","C4"
    )

    var selectedSeat by remember { mutableStateOf("") }
    var bookedSeats by remember { mutableStateOf(listOf<String>()) }
    // Auto-initialize all seats if they don't exist
    LaunchedEffect(Unit) {
        seats.forEach { seat ->
            val doc = db.collection("seats").document(seat).get().await()
            if (!doc.exists()) {
                db.collection("seats").document(seat).set(mapOf("booked" to false))
            }
        }
    }

    LaunchedEffect(Unit) {

        db.collection("seats")
            .addSnapshotListener { value, error ->

                val temp = mutableListOf<String>()

                value?.documents?.forEach {
                    val booked =
                        it.getBoolean("booked") ?: false

                    if (booked) {
                        temp.add(it.id)
                    }
                }

                bookedSeats = temp
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "🎟 Book Seat Online",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        seats.chunked(4).forEach { row ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                row.forEach { seat ->

                    val color =
                        when {
                            bookedSeats.contains(seat) -> Color.Red
                            selectedSeat == seat -> Color.Yellow
                            else -> Color.Green
                        }

                    Card(
                        modifier = Modifier
                            .size(65.dp)
                            .clickable {
                                if (!bookedSeats.contains(seat)) {
                                    selectedSeat = seat
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = color
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(seat)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {

                if (selectedSeat.isNotEmpty()) {

                    val bookedSeat = selectedSeat

                    db.collection("seats")
                        .document(bookedSeat)
                        .set(
                            mapOf("booked" to true)
                        )

                    selectedSeat = ""

                    navController.navigate("ticket/$bookedSeat")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Booking")
        }
    }
}