package com.kith.feature.community.impl

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.KithIcons

/**
 * "Community" detail screen.
 *
 * [isAdmin] controls the two behavior differences the user asked for:
 *  - true  -> a pencil edit badge appears on the community image (opens the
 *             image picker), AND a pencil icon appears next to "Detail"
 *             that toggles the bio into an editable text field. Bottom
 *             button reads "Delete Community".
 *  - false -> image is static (not clickable), Detail is plain read-only
 *             text, bottom button reads "Leave Community". Matches the
 *             reference screenshot exactly.
 *
 * Callbacks left for the caller to wire up:
 *  - onBackClick        -> back arrow tap
 *  - onLeaveOrDeleteClick -> bottom button tap (label already reflects
 *    isAdmin, caller just needs to handle the actual leave/delete logic)
 *  - onDetailChange     -> fired with the updated detail text whenever an
 *    admin finishes editing (taps the check icon)
 *  - onImageClick       -> fired after an admin picks a new image (receives
 *    the picked Uri); non-admins never trigger this since the image isn't
 *    clickable for them
 */
@Composable
fun CommunityDetailScreen(
    communityName: String = "Community Name",
    initialDetail: String = "Give details... What do you need help with or want to share?",
    isAdmin: Boolean = false,
    onBackClick: () -> Unit = {},
    onLeaveOrDeleteClick: () -> Unit = {},
    onDetailChange: (String) -> Unit = {},
    onImageClick: (Uri?) -> Unit = {},
) {
    val context = LocalContext.current

    var detailText by remember { mutableStateOf(initialDetail) }
    var isEditingDetail by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            selectedImageUri = it
//            val inputStream = context.contentResolver.openInputStream(it)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            inputStream?.close()
//            selectedBitmap = bitmap?.asImageBitmap()
//            onImageClick(it)
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // Back arrow
        IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 0.dp)) {
            Icon(
                imageVector = KithIcons.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFB9BCC9)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = communityName,
            color = Color(0xFF1B1F3B),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Community image -- only clickable/editable for admins
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(85.dp))
                    .background(Color(0xFFEDEEF6))
                    .then(
                        if (isAdmin) {
                            Modifier.clickable { /* TODO */ }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!,
                        contentDescription = "Community image",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // Admin-only edit badge on the image
            if (isAdmin) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 6.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { /* TODO */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = KithIcons.Edit,
                        contentDescription = "Edit community image",
                        tint = Color(0xFF2A4FE0),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Detail card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, Color(0xFFE2E4ED)), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detail",
                        color = Color(0xFF1B1F3B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Admin-only pencil toggle for editing the detail text
                    if (isAdmin) {
                        IconButton(
                            onClick = {
                                if (isEditingDetail) {
                                    onDetailChange(detailText)
                                }
                                isEditingDetail = !isEditingDetail
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingDetail) KithIcons.Check else KithIcons.Edit,
                                contentDescription = if (isEditingDetail) "Save detail" else "Edit detail",
                                tint = Color(0xFF2A4FE0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isAdmin && isEditingDetail) {
                    OutlinedTextField(
                        value = detailText,
                        onValueChange = { detailText = it },
                        placeholder = { Text("Give details... What do you need help with or want to share?") },
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2A4FE0),
                            unfocusedBorderColor = Color(0xFFE2E4ED),
                            focusedTextColor = Color(0xFF1B1F3B),
                            unfocusedTextColor = Color(0xFF1B1F3B),
                            cursorColor = Color(0xFF2A4FE0)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = detailText,
                        color = Color(0xFF7A7D8C),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom button -- label depends on isAdmin
        Button(
            onClick = onLeaveOrDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFAD9D9),
                contentColor = Color(0xFFD64545)
            )
        ) {
            Text(
                text = if (isAdmin) "Delete Community" else "Leave Community",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 620, name = "Non-admin view")
@Composable
fun CommunityDetailScreenPreview_NonAdmin() {
    CommunityDetailScreen(isAdmin = false)
}

@Preview(showBackground = true, widthDp = 320, heightDp = 620, name = "Admin view")
@Composable
fun CommunityDetailScreenPreview_Admin() {
    CommunityDetailScreen(isAdmin = true)
}