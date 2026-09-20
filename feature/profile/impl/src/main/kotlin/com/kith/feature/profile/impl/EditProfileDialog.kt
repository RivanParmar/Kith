package com.kith.feature.profile.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kith.core.designsystem.icon.KithIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileDialog(
    initialName: String,
    initialBio: String,
    initialImageUrl: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, imageUrl: String?) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(KithIcons.Add, contentDescription = "Close")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { onSave(name, bio, imageUrl) },
                            enabled = name.isNotBlank(),
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )
            },
            containerColor = Color(0xFFF8F9FA),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center,
                    ) {
                        // TODO: Use AsyncImage here when implementing real images
                        Icon(KithIcons.Add, contentDescription = null, tint = Color(0xFF94A3B8))
                    }
                    SmallFloatingActionButton(
                        onClick = { /* TODO: Launch Photo Picker */ },
                        shape = CircleShape,
                        containerColor = Color(0xFF3B82F6),
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            KithIcons.Add,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        focusedLabelColor = Color(0xFF3B82F6),
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        focusedLabelColor = Color(0xFF3B82F6),
                    ),
                )
            }
        }
    }
}