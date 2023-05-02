package com.wit.homegrownapp.ui.receiver// In com.wit.homegrownapp.ui.receiver.NotificationReceiver.kt
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wit.homegrownapp.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "New Order"
        val message = intent.getStringExtra("message") ?: "You have received a new order from HomeGrown!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } else {
            // Handle the case when permission is not granted.
        }
    }

    companion object {
        const val CHANNEL_ID = "HomeGrown_Channel"
        const val NOTIFICATION_ID = 1
    }
}
