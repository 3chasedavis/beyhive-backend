package com.beyhivealert.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.beyhivealert.android.MainActivity
import com.beyhivealert.android.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "beyhive_alerts"
        private const val CHANNEL_NAME = "Beyhive Alerts"
        private const val CHANNEL_DESCRIPTION = "Notifications from Beyhive Alert"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("New FCM token: $token")
        
        // Send token to backend with platform identification
        sendTokenToBackend(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        println("Message received from: ${remoteMessage.from}")
        
        // Create notification channel for Android 8.0+
        createNotificationChannel()
        
        // Show notification
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "Beyhive Alert",
                message = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add any data you want to pass to the main activity
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.bee_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun sendTokenToBackend(token: String) {
        // Send the FCM token to your backend with platform identification
        println("Sending FCM token to backend: $token")
        
        // Use a coroutine to make the API call
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val url = URL("https://beyhive-backend.onrender.com/register-device")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val body = mapOf(
                    "deviceToken" to token,
                    "platform" to "android",
                    "preferences" to mapOf(
                        "beyonceOnStage" to true,
                        "concertStart" to true,
                        "americaHasAProblem" to true,
                        "tyrant" to true,
                        "lastAct" to true,
                        "sixteenCarriages" to true,
                        "amen" to true
                    )
                )
                
                val jsonBody = org.json.JSONObject(body).toString()
                connection.outputStream.use { os ->
                    os.write(jsonBody.toByteArray())
                    os.flush()
                }
                
                val responseCode = connection.responseCode
                println("Token registration response code: $responseCode")
                
                if (responseCode == 200) {
                    println("FCM token successfully registered with backend")
                } else {
                    println("Failed to register FCM token with backend")
                }
            } catch (e: Exception) {
                println("Error sending FCM token to backend: ${e.message}")
            }
        }
    }
}
