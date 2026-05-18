package com.example.nammamela.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val currentUid = auth.currentUser?.uid ?: ""

    var dramaName by remember { mutableStateOf("") }
    var showTime by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var showAdminUid by remember { mutableStateOf("") }
    val isShowAdmin = currentUid == showAdminUid && currentUid.isNotEmpty()

    // Cast fields
    var castName1 by remember { mutableStateOf("") }
    var castRole1 by remember { mutableStateOf("") }
    var castName2 by remember { mutableStateOf("") }
    var castRole2 by remember { mutableStateOf("") }
    var castName3 by remember { mutableStateOf("") }
    var castRole3 by remember { mutableStateOf("") }

    // Load existing show + cast data
    LaunchedEffect(Unit) {
        db.collection("show").document("today").get()
            .addOnSuccessListener { doc ->
                dramaName = doc.getString("name") ?: ""
                showTime = doc.getString("time") ?: ""
                venue = doc.getString("venue") ?: ""
                showAdminUid = doc.getString("adminUid") ?: ""
            }

        db.collection("cast").orderBy("order").get()
            .addOnSuccessListener { snap ->
                snap.documents.getOrNull(0)?.let {
                    castName1 = it.getString("name") ?: ""
                    castRole1 = it.getString("role") ?: ""
                }
                snap.documents.getOrNull(1)?.let {
                    castName2 = it.getString("name") ?: ""
                    castRole2 = it.getString("role") ?: ""
                }
                snap.documents.getOrNull(2)?.let {
                    castName3 = it.getString("name") ?: ""
                    castRole3 = it.getString("role") ?: ""
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(text = "👑 Admin Panel", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Show who owns this show
        if (showAdminUid.isNotEmpty() && !isShowAdmin) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "⚠️ You can view but not edit — another user created this show.",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(text = "🎭 Show Details", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dramaName,
            onValueChange = { if (isShowAdmin || showAdminUid.isEmpty()) dramaName = it },
            label = { Text("Drama Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isShowAdmin || showAdminUid.isEmpty()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = showTime,
            onValueChange = { if (isShowAdmin || showAdminUid.isEmpty()) showTime = it },
            label = { Text("Show Time") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isShowAdmin || showAdminUid.isEmpty()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = venue,
            onValueChange = { if (isShowAdmin || showAdminUid.isEmpty()) venue = it },
            label = { Text("Venue") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isShowAdmin || showAdminUid.isEmpty()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (isShowAdmin || showAdminUid.isEmpty()) {
                    db.collection("show").document("today")
                        .set(mapOf(
                            "name" to dramaName,
                            "time" to showTime,
                            "venue" to venue,
                            "adminUid" to currentUid // Save creator's UID
                        ))
                        .addOnSuccessListener {
                            showAdminUid = currentUid
                            Toast.makeText(context, "Show Updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Only the show creator can edit!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isShowAdmin || showAdminUid.isEmpty()
        ) {
            Text("Update Show")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Cast section — only visible/editable to show admin
        Text(text = "🎬 Manage Cast", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (!isShowAdmin && showAdminUid.isNotEmpty()) {
            Text(
                text = "🔒 Only the show creator can manage cast.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            listOf(
                Triple("Cast Member 1", castName1, castRole1) to { n: String, r: String ->
                    castName1 = n; castRole1 = r
                },
                Triple("Cast Member 2", castName2, castRole2) to { n: String, r: String ->
                    castName2 = n; castRole2 = r
                },
                Triple("Cast Member 3", castName3, castRole3) to { n: String, r: String ->
                    castName3 = n; castRole3 = r
                }
            ).forEach { (triple, setter) ->
                val (label, name, role) = triple
                Text(text = label, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { setter(it, role) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = role,
                    onValueChange = { setter(name, it) },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = {
                    val castList = listOf(
                        mapOf("name" to castName1, "role" to castRole1, "order" to 0),
                        mapOf("name" to castName2, "role" to castRole2, "order" to 1),
                        mapOf("name" to castName3, "role" to castRole3, "order" to 2)
                    )
                    // Delete old cast and write new
                    db.collection("cast").get().addOnSuccessListener { snap ->
                        snap.documents.forEach { it.reference.delete() }
                        castList.forEachIndexed { index, cast ->
                            db.collection("cast").document("member$index").set(cast)
                        }
                        Toast.makeText(context, "Cast Updated!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Cast")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isShowAdmin || showAdminUid.isEmpty()) {
                    db.collection("seats").get()
                        .addOnSuccessListener { result ->
                            result.documents.forEach {
                                db.collection("seats").document(it.id).delete()
                            }
                            Toast.makeText(context, "Seats Reset!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Reset Failed", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Only the show creator can reset seats!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isShowAdmin || showAdminUid.isEmpty()
        ) {
            Text("Reset All Seats")
        }
    }
}