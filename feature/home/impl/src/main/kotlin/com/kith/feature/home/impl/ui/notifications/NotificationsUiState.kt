package com.kith.feature.home.impl.ui.notifications

import com.kith.core.model.data.Notification

sealed interface NotificationsUiState {
    /** Screen is waiting for data to load. */
    data object Loading : NotificationsUiState

    /** Notifications loaded successfully. */
    data class Success(val notifications: List<Notification>) : NotificationsUiState

    /** An error occurred while retrieving notifications. */
    data class Error(val message: String) : NotificationsUiState
}