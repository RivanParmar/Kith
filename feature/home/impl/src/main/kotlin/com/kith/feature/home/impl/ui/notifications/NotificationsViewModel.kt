package com.kith.feature.home.impl.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.NotificationRepository
import com.kith.core.model.data.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val uiState: StateFlow<NotificationsUiState> = notificationRepository.getNotifications()
        .map<List<Notification>, NotificationsUiState> { notifications ->
            NotificationsUiState.Success(notifications)
        }
        .catch { throwable ->
            emit(NotificationsUiState.Error(throwable.message ?: "Failed to load notifications"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationsUiState.Loading
        )

    fun markAsRead(id: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markNotificationAsRead(id)
            } catch (e: Exception) {
                // Handle/log error if needed
            }
        }
    }
}