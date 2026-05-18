package com.example.nammamela.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FanWallScreen() {

    val db = FirebaseFirestore.getInstance()

    var comment by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {

        db.collection("comments")
            .addSnapshotListener { value, error ->

                val temp = mutableListOf<String>()

                value?.documents?.forEach {
                    val msg =
                        it.getString("text") ?: ""

                    temp.add(msg)
                }

                comments = temp
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "👏 Fan Wall",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Write your comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                if (comment.isNotEmpty()) {

                    db.collection("comments")
                        .add(
                            mapOf("text" to comment)
                        )

                    comment = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post Comment")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(comments) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}