package com.kith.feature.home.impl.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.model.data.Notification
import com.kith.feature.home.api.R
import kotlin.time.Clock

@Composable
fun NotificationsRoute(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        uiState = uiState,
        onNotificationClick = { notification ->
            viewModel.markAsRead(notification.id)
            onNavigateToPost(notification.postId)
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
internal fun NotificationsScreen(
    uiState: NotificationsUiState,
    onNotificationClick: (Notification) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            KithMediumTopAppBar(
                titleRes = R.string.feature_notifications_api_title,
                navigationIcon = KithIcons.ArrowBack,
                navigationIconContentDescription = "Back",
                actionIcon = null,
                actionIconContentDescription = null,
                scrollBehavior = scrollBehavior,
                onNavigationClick = onBackClick,
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (uiState) {
                is NotificationsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingWheel(contentDesc = "Loading notifications")
                    }
                }

                is NotificationsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                is NotificationsUiState.Success -> {
                    if (uiState.notifications.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notifications yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.notifications,
                                key = { it.id }
                            ) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = { onNotificationClick(notification) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (notification.isRead) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }

    val contentColor = if (notification.isRead) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    val subtitleColor = if (notification.isRead) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.body,
                fontSize = 14.sp,
                color = subtitleColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    MaterialTheme {
        NotificationsScreen(
            uiState = NotificationsUiState.Success(
                listOf(
                    Notification(
                        id = "1",
                        title = "Stuck on React Hook state update bug",
                        body = "1,240 active members",
                        postId = "1",
                        timestamp = Clock.System.now(),
                        isRead = false,
                    ),
                    Notification(
                        id = "2",
                        title = "How to center a div in Tailwind CSS",
                        body = "1,000 active members",
                        postId = "2",
                        timestamp = Clock.System.now(),
                        isRead = true,
                    )
                )
            ),
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenEmptyPreview() {
    MaterialTheme {
        NotificationsScreen(
            uiState = NotificationsUiState.Success(emptyList()),
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}