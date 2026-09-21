package com.kith.feature.profile.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.UserProfile
import com.kith.feature.profile.api.R
import com.kith.feature.profile.impl.settings.SettingsDialog
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onWalletClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}, // Added navigation callback
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        modifier = modifier,
        onWalletClick = onWalletClick,
        onLogoutClick = {
            viewModel.logout(onSuccess = onNavigateToLogin)
        },
        onEditProfile = viewModel::saveProfile,
    )
}

@Composable
internal fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onWalletClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditProfile: (name: String, bio: String, imageUrl: String?) -> Unit = { _, _, _, -> },
) {
    when (uiState) {
        ProfileUiState.Loading -> {
            LoadingState(modifier = modifier)
        }

        is ProfileUiState.Error -> {
            ErrorState(uiState.message, modifier = modifier)
        }

        is ProfileUiState.Success -> {
            var openEditProfileDialog by remember { mutableStateOf(false) }
            var openSettingsDialog by remember { mutableStateOf(false) }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.feature_profile_api_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                        )
                        // TODO: Replace Box with AsyncImage or Image when implementing image loading

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                text = uiState.userProfile.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                            ) {
                                Icon(
                                    imageVector = KithIcons.StarRate,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.userProfile.rating.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }

                            OutlinedButton(
                                onClick = { openEditProfileDialog = true },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = KithIcons.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit Profile",
                                    fontSize = 14.sp,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ProfileStatCard(
                        value = uiState.userProfile.problemsAsked.toString(),
                        label = "ASKED",
                        valueColor = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ProfileStatCard(
                        value = uiState.userProfile.problemsSolved.toString(),
                        label = "SOLVED",
                        valueColor = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ProfileStatCard(
                        value = formatXp(uiState.userProfile.xp),
                        label = "XP EARNED",
                        valueColor = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f),
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = KithIcons.AccountBalanceWallet,
                            label = "Transactions",
                            onClick = onWalletClick,
                        )

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Add,
                            label = "Upgrade to Premium",
                            labelColor = Color(0xFF3B82F6),
                            backgroundColor = Color(0xFFEFF6FF),
                            showProBadge = true,
                            onClick = { /* TODO */ },
                        )

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Settings,
                            label = "Settings",
                            onClick = {
                                openSettingsDialog = true
                            }
                        )

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Groups,
                            label = "Community Settings",
                            onClick = { /* TODO */ },
                        )
                    }
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFEF2F2)),
                ) {
                    Icon(
                        imageVector = KithIcons.Logout,
                        contentDescription = "Log Out",
                        tint = Color(0xFFEF4444),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444),
                    )
                }
            }

            if (openSettingsDialog) {
                SettingsDialog(
                    onDismiss = { openSettingsDialog = false }
                )
            }

            if (openEditProfileDialog) {
                EditProfileDialog(
                    initialName = uiState.userProfile.name,
                    initialBio = uiState.userProfile.bio ?: "",
                    initialImageUrl = uiState.userProfile.profileImageUrl,
                    onDismiss = { openEditProfileDialog = false },
                    onSave = onEditProfile,
                )
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Card(
        modifier = modifier.height(88.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = Color(0xFF0F172A),
    backgroundColor: Color = Color.Transparent,
    showProBadge: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = labelColor,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier.weight(1f),
        )

        if (showProBadge) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF3B82F6), shape = RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "PRO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Icon(
            imageVector = KithIcons.ChevronForward,
            contentDescription = "Navigate",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LoadingWheel(
            contentDesc = "Loading profile",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = error,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

private fun formatXp(xp: Int): String {
    return if (xp >= 1000) {
        String.format(Locale.US, "%.1fK", xp / 1000.0)
    } else {
        xp.toString()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    KithTheme {
        ProfileScreen(
            uiState = ProfileUiState.Success(
                UserProfile(
                    id = "u_1",
                    name = "Elena Rostova",
                    profileImageUrl = null,
                    bio = "University Student",
                    xp = 2400,
                    rating = 4.9f,
                    problemsAsked = 12,
                    problemsSolved = 42,
                    isPremium = true
                )
            ),
            onWalletClick = {},
            onLogoutClick = {},
        )
    }
}