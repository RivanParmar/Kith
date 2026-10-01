package com.kith.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF2563EB)
val LightBlue = Color(0xFF5B8DF9)
val SecondaryBlue = Color(0xFF5180EC)
val DarkNavy = Color(0xFF0F172A)
val ErrorOrange = Color(0xFFFF6B00)
val ErrorRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)
val WarningGold = Color(0xFFF59E0B)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFEFF6FF)
val OnPrimaryContainerLight = Color(0xFF1E3A8A)
val SecondaryContainerLight = Color(0xFFEEF2FF)
val OnSecondaryContainerLight = Color(0xFF2563EB)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF64748B)
val OutlineLight = Color(0xFFE2E8F0)
val OutlineVariantLight = Color(0xFFCBD5E1)

val BackgroundDark = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF111827)
val PrimaryContainerDark = Color(0xFF1E293B)
val OnPrimaryContainerDark = Color(0xFF93C5FD)
val SecondaryContainerDark = Color(0xFF1E2238)
val OnSecondaryContainerDark = Color(0xFFBFDBFE)
val SurfaceVariantDark = Color(0xFF1E293B)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val OutlineDark = Color(0xFF334155)
val OutlineVariantDark = Color(0xFF475569)

val GradientBlueStart = Color(0xFF2563EB)
val GradientBlueEnd = Color(0xFF5B8DF9)
val GradientBlueSoft = Color(0xFFB7C9FA)

@Immutable
data class KithExtendedColors(
    val authBackground: Color,
    val authTitleColor: Color,
    val authDesignColor: Color,
    val authWaveColor: Color,
    val authWaveBackgroundColor: Color,
    val tabTrackBg: Color,
    val tabPillBg: Color,
    val tabTextSelected: Color,
    val tabTextUnselected: Color,
    val paywallBg: Color,
    val paywallTextPrimary: Color,
    val paywallTextSecondary: Color,
    val paywallPillTrackBg: Color,
    val paywallPillTrackBorder: Color,
    val dailyRewardBg: Color,
    val dailyRewardAccent: Color,
    val dailyRewardTextPrimary: Color,
    val dailyRewardTextSecondary: Color,
    val dailyRewardCenterCircle: Color,
    val dailyRewardRingTrack: Color,
    val dailyRewardButtonBg: Color,
    val dailyRewardButtonText: Color,
    val dailyRewardIconFuture: Color,
    val historyCardBg: Color,
    val historyCardBorder: Color,
    val historyTitleText: Color,
    val historySubtitleText: Color,
    val historyTimeText: Color,
    val historyBadgeActiveBg: Color,
    val historyBadgeActiveText: Color,
    val historyBadgeInactiveBg: Color,
    val historyBadgeInactiveText: Color,
    val historyXpActiveText: Color,
    val historyXpInactiveText: Color,
    val transactionCardBg: Color,
    val transactionCardBorder: Color,
    val transactionTitleText: Color,
    val transactionDateText: Color,
    val transactionPositiveBg: Color,
    val transactionPositiveText: Color,
    val transactionNegativeBg: Color,
    val transactionNegativeText: Color,
    val profileBg: Color,
    val profileCardBg: Color,
    val profileCardBorder: Color,
    val profileTextPrimary: Color,
    val profileTextSecondary: Color,
    val profileDivider: Color,
    val profileIconNeutral: Color,
    val profileRatingStar: Color,
    val profileActionBlue: Color,
    val profileActionBlueBg: Color,
    val profileProBadgeBg: Color,
    val profileProBadgeText: Color,
    val profileLogoutBg: Color,
    val profileLogoutBorder: Color,
    val profileLogoutText: Color,
    val postCardStar: Color,
    val postCardRatingText: Color,
    val postCardBadgeBg: Color,
    val postCardBadgeText: Color,
    val postCardContentText: Color,
    val postCardDateText: Color,
    val detailBg: Color,
    val detailTextPrimary: Color,
    val detailTextSecondary: Color,
    val detailCardBg: Color,
    val detailStar: Color,
    val detailTagBg: Color,
    val detailTagText: Color,
    val detailBountyBg: Color,
    val detailBountyBorder: Color,
    val detailBountyTitle: Color,
    val detailBountyXp: Color,
    val detailPrimaryBlue: Color,
    val detailSolutionBg: Color,
    val detailBorderLight: Color,
)

val LocalKithExtendedColors = staticCompositionLocalOf {
    KithExtendedColors(
        authBackground = Color.Unspecified,
        authTitleColor = Color.Unspecified,
        authDesignColor = Color.Unspecified,
        authWaveColor = Color.Unspecified,
        authWaveBackgroundColor = Color.Unspecified,
        tabTrackBg = Color.Unspecified,
        tabPillBg = Color.Unspecified,
        tabTextSelected = Color.Unspecified,
        tabTextUnselected = Color.Unspecified,
        paywallBg = Color.Unspecified,
        paywallTextPrimary = Color.Unspecified,
        paywallTextSecondary = Color.Unspecified,
        paywallPillTrackBg = Color.Unspecified,
        paywallPillTrackBorder = Color.Unspecified,
        dailyRewardBg = Color.Unspecified,
        dailyRewardAccent = Color.Unspecified,
        dailyRewardTextPrimary = Color.Unspecified,
        dailyRewardTextSecondary = Color.Unspecified,
        dailyRewardCenterCircle = Color.Unspecified,
        dailyRewardRingTrack = Color.Unspecified,
        dailyRewardButtonBg = Color.Unspecified,
        dailyRewardButtonText = Color.Unspecified,
        dailyRewardIconFuture = Color.Unspecified,
        historyCardBg = Color.Unspecified,
        historyCardBorder = Color.Unspecified,
        historyTitleText = Color.Unspecified,
        historySubtitleText = Color.Unspecified,
        historyTimeText = Color.Unspecified,
        historyBadgeActiveBg = Color.Unspecified,
        historyBadgeActiveText = Color.Unspecified,
        historyBadgeInactiveBg = Color.Unspecified,
        historyBadgeInactiveText = Color.Unspecified,
        historyXpActiveText = Color.Unspecified,
        historyXpInactiveText = Color.Unspecified,
        transactionCardBg = Color.Unspecified,
        transactionCardBorder = Color.Unspecified,
        transactionTitleText = Color.Unspecified,
        transactionDateText = Color.Unspecified,
        transactionPositiveBg = Color.Unspecified,
        transactionPositiveText = Color.Unspecified,
        transactionNegativeBg = Color.Unspecified,
        transactionNegativeText = Color.Unspecified,
        profileBg = Color.Unspecified,
        profileCardBg = Color.Unspecified,
        profileCardBorder = Color.Unspecified,
        profileTextPrimary = Color.Unspecified,
        profileTextSecondary = Color.Unspecified,
        profileDivider = Color.Unspecified,
        profileIconNeutral = Color.Unspecified,
        profileRatingStar = Color.Unspecified,
        profileActionBlue = Color.Unspecified,
        profileActionBlueBg = Color.Unspecified,
        profileProBadgeBg = Color.Unspecified,
        profileProBadgeText = Color.Unspecified,
        profileLogoutBg = Color.Unspecified,
        profileLogoutBorder = Color.Unspecified,
        profileLogoutText = Color.Unspecified,
        postCardStar = Color.Unspecified,
        postCardRatingText = Color.Unspecified,
        postCardBadgeBg = Color.Unspecified,
        postCardBadgeText = Color.Unspecified,
        postCardContentText = Color.Unspecified,
        postCardDateText = Color.Unspecified,
        detailBg = Color.Unspecified,
        detailTextPrimary = Color.Unspecified,
        detailTextSecondary = Color.Unspecified,
        detailCardBg = Color.Unspecified,
        detailStar = Color.Unspecified,
        detailTagBg = Color.Unspecified,
        detailTagText = Color.Unspecified,
        detailBountyBg = Color.Unspecified,
        detailBountyBorder = Color.Unspecified,
        detailBountyTitle = Color.Unspecified,
        detailBountyXp = Color.Unspecified,
        detailPrimaryBlue = Color.Unspecified,
        detailSolutionBg = Color.Unspecified,
        detailBorderLight = Color.Unspecified,
    )
}
