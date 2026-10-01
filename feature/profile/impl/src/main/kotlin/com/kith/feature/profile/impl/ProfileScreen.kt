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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.UserProfile
import com.kith.core.ui.ProfileAvatar
import com.kith.feature.profile.api.R
import com.kith.feature.profile.impl.settings.SettingsDialog
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onWalletClick: () -> Unit = {},
    onPostHistoryClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        modifier = modifier,
        onWalletClick = onWalletClick,
        onPostHistoryClick = onPostHistoryClick,
        onLogoutClick = {
            viewModel.logout(onSuccess = onNavigateToLogin)
        },
        onEditProfile = viewModel::saveProfile,
        onNavigateToPremium = onNavigateToPremium,
        onNavigateToCommunity = onNavigateToCommunity,
    )
}

@Composable
internal fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onWalletClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onPostHistoryClick: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onEditProfile: (name: String, bio: String, imageUrl: String?) -> Unit = { _, _, _, -> },
) {
    val extendedColors = KithTheme.extendedColors

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

            val scrollState = rememberScrollState()

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(extendedColors.profileBg) // Themed Background
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.feature_profile_api_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = extendedColors.profileTextPrimary, // Themed Text
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    fontFamily = OutfitFontFamily,
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = extendedColors.profileCardBg),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, extendedColors.profileCardBorder),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProfileAvatar(
                            userProfile = uiState.userProfile,
                            avatarSize = 72.dp,
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                text = uiState.userProfile.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.profileTextPrimary,
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                            ) {
                                Icon(
                                    imageVector = KithIcons.StarRate,
                                    contentDescription = "Rating",
                                    tint = extendedColors.profileRatingStar, // Themed Star
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.userProfile.rating.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = extendedColors.profileTextPrimary,
                                )
                            }

                            OutlinedButton(
                                onClick = { openEditProfileDialog = true },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, extendedColors.profileActionBlue),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = KithIcons.Edit,
                                    contentDescription = "Edit",
                                    tint = extendedColors.profileActionBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit Profile",
                                    fontSize = 14.sp,
                                    color = extendedColors.profileActionBlue,
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
                        valueColor = extendedColors.profileTextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ProfileStatCard(
                        value = uiState.userProfile.problemsSolved.toString(),
                        label = "SOLVED",
                        valueColor = extendedColors.profileTextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ProfileStatCard(
                        value = formatXp(uiState.userProfile.xp),
                        label = "XP EARNED",
                        valueColor = extendedColors.profileActionBlue,
                        modifier = Modifier.weight(1f),
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = extendedColors.profileCardBg),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, extendedColors.profileCardBorder),
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = KithIcons.AccountBalanceWallet,
                            label = "Transactions",
                            onClick = onWalletClick,
                        )

                        HorizontalDivider(color = extendedColors.profileDivider, thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Crown,
                            label = "Post History",
                            onClick = onPostHistoryClick,
                        )

                        HorizontalDivider(color = extendedColors.profileDivider, thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Crown,
                            label = "Upgrade to Premium",
                            labelColor = extendedColors.profileActionBlue,
                            backgroundColor = extendedColors.profileActionBlueBg,
                            showProBadge = true,
                            onClick = onNavigateToPremium,
                        )

                        HorizontalDivider(color = extendedColors.profileDivider, thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Settings,
                            label = "Settings",
                            onClick = {
                                openSettingsDialog = true
                            }
                        )

                        HorizontalDivider(color = extendedColors.profileDivider, thickness = 1.dp)

                        ProfileMenuItem(
                            icon = KithIcons.Groups,
                            label = "Community Settings",
                            onClick = onNavigateToCommunity,
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
                    border = BorderStroke(1.dp, extendedColors.profileLogoutBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = extendedColors.profileLogoutBg),
                ) {
                    Icon(
                        imageVector = KithIcons.Logout,
                        contentDescription = "Log Out",
                        tint = extendedColors.profileLogoutText,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.profileLogoutText,
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
                    onSave = { name, bio, imageUrl ->
                        onEditProfile(name, bio, imageUrl)
                        openEditProfileDialog = false },
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
    val extendedColors = KithTheme.extendedColors // Pull theme colors

    Card(
        modifier = modifier.height(88.dp),
        colors = CardDefaults.cardColors(containerColor = extendedColors.profileCardBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, extendedColors.profileCardBorder),
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
                color = extendedColors.profileTextSecondary, // Themed Label
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    labelColor: Color? = null,
    backgroundColor: Color = Color.Transparent,
    showProBadge: Boolean = false,
) {
    val extendedColors = KithTheme.extendedColors // Pull theme colors
    val finalLabelColor = labelColor ?: extendedColors.profileTextPrimary // Fallback to themed text

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
            tint = finalLabelColor,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = finalLabelColor,
            modifier = Modifier.weight(1f),
        )

        if (showProBadge) {
            Box(
                modifier = Modifier
                    .background(extendedColors.profileProBadgeBg, shape = RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "PRO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.profileProBadgeText,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Icon(
            imageVector = KithIcons.ChevronForward,
            contentDescription = "Navigate",
            tint = extendedColors.profileIconNeutral, // Themed chevron
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
            onPostHistoryClick = {},
            onLogoutClick = {},
        )
    }
}