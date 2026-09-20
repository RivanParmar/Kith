package com.kith.feature.profile.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.DarkNavy
import com.kith.core.designsystem.theme.InterFontFamily
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.designsystem.theme.PrimaryBlue
import com.kith.core.model.data.UserProfile

@Composable
fun OtherUserProfileScreen(
    userProfile: UserProfile,
    onTagClick: () -> Unit = {},
    onRequestClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val topBackgroundColor = if (isDark) DarkNavy else PrimaryBlue
    val bottomBackgroundColor = if (isDark) Color(0xFF1F2937) else Color.White
    val bottomTextColor = if (isDark) Color.White else DarkNavy
    val cardBackgroundColor = if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6)
    val cardTextColor = if (isDark) Color(0xFFD1D5DB) else Color(0xFF4B5563)
    val dividerColor = if (isDark) Color(0xFF4B5563) else Color(0xFFE5E7EB)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bottomBackgroundColor)
    ) {
        // Wave background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.7f)
                quadraticBezierTo(
                    size.width * 0.75f, size.height * 0.95f,
                    size.width * 0.5f, size.height * 0.85f
                )
                quadraticBezierTo(
                    size.width * 0.25f, size.height * 0.75f,
                    0f, size.height * 0.9f
                )
                close()
            }
            drawPath(path, color = topBackgroundColor)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                    .border(4.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = KithIcons.PersonOutlined,
                    contentDescription = "Default Profile",
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val displayName = userProfile.name.takeIf { it.isNotBlank() } ?: "Name"
            Text(
                text = displayName,
                fontFamily = OutfitFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = "@${userProfile.id}",
                fontFamily = InterFontFamily,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onTagClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Tag",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Lower Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Statistics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val level = (userProfile.xp / 1000) + 1
                    OtherUserProfileStatItem(value = "Level $level", label = "Rank", color = bottomTextColor)
                    OtherUserProfileStatItem(value = userProfile.xp.toString(), label = "Total XP", color = bottomTextColor)
                    OtherUserProfileStatItem(value = userProfile.problemsSolved.toString(), label = "connection", color = bottomTextColor)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = dividerColor)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Bio",
                    fontFamily = OutfitFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = bottomTextColor
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackgroundColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val bioText = userProfile.bio?.takeIf { it.isNotBlank() } ?: "Stuck on React Hook state update bug"
                    Text(
                        text = bioText,
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        color = cardTextColor,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
//            val requestButtonColor = when (connectionState) {
//                ConnectionState.NOT_CONNECTED -> if (isDark) PrimaryBlue else Color.Black
//                ConnectionState.PENDING_SENT -> Color.Gray
//                ConnectionState.PENDING_RECEIVED -> PrimaryBlue
//                ConnectionState.CONNECTED -> Color(0xFF10B981) // Green
//            }
            val requestButtonTextColor = Color.White
//            val buttonText = when (connectionState) {
//                ConnectionState.NOT_CONNECTED -> "SEND REQUEST"
//                ConnectionState.PENDING_SENT -> "REQUEST SENT"
//                ConnectionState.PENDING_RECEIVED -> "ACCEPT REQUEST"
//                ConnectionState.CONNECTED -> "CONNECTED"
//            }
            
            Button(
                onClick = onRequestClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = requestButtonTextColor
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "CONNECTED",
                    fontFamily = InterFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun OtherUserProfileStatItem(value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = OutfitFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            color = color.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OtherUserProfileScreenPreview() {
    com.kith.core.designsystem.theme.KithTheme {
        OtherUserProfileScreen(
            userProfile = UserProfile(
                id = "id",
                name = "Name",
                profileImageUrl = null,
                bio = "Stuck on React Hook state update bug",
                xp = 3150,
                rating = 4.9f,
                problemsAsked = 12,
                problemsSolved = 250,
                isPremium = true
            )
        )
    }
}
