package com.kith.core.notifications

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import com.kith.core.model.data.Notification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TARGET_ACTIVITY_NAME = "com.kith.MainActivity"
private const val POST_NOTIFICATION_REQUEST_CODE = 0
private const val POST_NOTIFICATION_CHANNEL_ID = ""
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.kith.com"
private const val DEEP_LINK_HOME_PATH = "home"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_HOME_PATH"
const val DEEP_LINK_POST_ID_KEY = "linkedPostId"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_POST_ID_KEY}"

@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postNotifications(
        notification: Notification,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return@with
        }
//        val channelId = "PostNotificationChannel"

//        val notificationId = notification.id.toIntOrNull() ?: notification.hashCode()

        val postNotification = createPostNotification {
//            .setSmallIcon(R.drawable.)
            setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(postsPendingIntent(notification.postId))
            .setAutoCancel(true)
        }

//        notificationManager.notify(notificationId, notificationBuilder.build())
        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(
            notification.id.toIntOrNull() ?: notification.hashCode(),
            postNotification,
        )
    }
}

/**
 * Creates a notification for configured for news updates
 */
private fun Context.createPostNotification(
    block: NotificationCompat.Builder.() -> Unit,
): android.app.Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        POST_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        POST_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notifications_post_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notifications_post_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.postsPendingIntent(
    postId: String,
): PendingIntent? = PendingIntent.getActivity(
    this,
    POST_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = "$DEEP_LINK_BASE_PATH/$postId".toUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)