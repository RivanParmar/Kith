package com.kith.feature.home.impl.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.NotificationRepository
import com.kith.core.model.data.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NotificationsUiState {
    data object Loading : NotificationsUiState
    data class Success(val notifications: List<Notification>) : NotificationsUiState
    data class Error(val message: String) : NotificationsUiState
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val current = notificationRepository.getNotifications().first()
            if (current.isEmpty()) {
                notificationRepository.insertNotifications(
                    listOf(
                        Notification(
                            id = "1",
                            title = "Stuck on React Hook state update bug",
                            memberCount = "1,240",
                            isRead = false
                        ),
                        Notification(
                            id = "2",
                            title = "How to center a div in Tailwind CSS",
                            memberCount = "3,450",
                            isRead = false
                        ),
                        Notification(
                            id = "3",
                            title = "Jetpack Compose recomposition issues",
                            memberCount = "890",
                            isRead = true
                        )
                    )
                )
            }
        }
    }

    val uiState: StateFlow<NotificationsUiState> = notificationRepository.getNotifications()
        .map { notifications ->
            NotificationsUiState.Success(notifications)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationsUiState.Loading
        )

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markNotificationAsRead(id)
        }
    }
}
