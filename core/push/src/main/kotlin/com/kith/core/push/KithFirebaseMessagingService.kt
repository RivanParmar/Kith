package com.kith.core.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kith.core.common.network.Dispatcher
import com.kith.core.common.network.KithDispatchers
import com.kith.core.data.repository.NotificationRepository
import com.kith.core.data.repository.UserRepository
import com.kith.core.notifications.Notifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KithFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notifier: Notifier

    @Inject
    @Dispatcher(KithDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    private val scope = CoroutineScope(SupervisorJob())

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            val title = message.data["title"] ?: return
            val body = message.data["body"] ?: return
            val postId = message.data["postId"] ?: return

            scope.launch(ioDispatcher) {
                val savedNotification = notificationRepository.insertNotification(
                    title = title,
                    body = body,
                    postId = postId,
                )

                notifier.postNotifications(savedNotification)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        scope.launch {
            userRepository.syncFcmToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}