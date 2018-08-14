package bab.com.build_a_bridge

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import bab.com.build_a_bridge.enums.BundleParamNames
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

class MessageNotificationService : FirebaseMessagingService(), AnkoLogger {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        debug { "From: ${remoteMessage?.from}" }

        remoteMessage?.let {message ->
            if(message.data.isNotEmpty()){
                debug { "Message data payload: ${message.data}" }
                if(message.notification != null){
                    debug { "Message notification body: ${remoteMessage.notification?.body}" }
                    remoteMessage.notification?.let {
                        sendNotification(it)
                    }
                }
            }
        }

    }

    fun sendNotification(notification: RemoteMessage.Notification) {

        if (notification.body != null && notification.title != null) {

            val title = notification.title!!
            val message = if(notification.body!!.length > 100) notification.body!!.substring(0 until 97) + "..."
            else notification.body!!


            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val bundle = Bundle()
            bundle.putBoolean(BundleParamNames.DIRECT_TO_CONVERSATIONS.toString(), true)
            intent.putExtra(BundleParamNames.MESSAGE_BUNDLE.toString(), bundle)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT)

            val channelId = getString(R.string.message_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notifcationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.bab_logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since Oreo notification channel is needed
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId,
                        getString(R.string.message_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(0, notifcationBuilder.build())
        }
    }
}
