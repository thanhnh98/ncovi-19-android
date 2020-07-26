package com.thanh.kotlintesting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.thanh.kotlintesting.models.NCoviModel
import com.thanh.kotlintesting.receiver.NcoviReceiver
import com.thanh.kotlintesting.service.ForegroundService
import com.thanh.kotlintesting.ui.MainActivity


class NotificationHelper(private val context: Context, private val data:NCoviModel) {


    companion object {
        private const val CHANNEL_ID = "Foreground"
        const val ACTION_UPDATE="update"
        const val ACTION_CLOSE="close"
    }

    fun builder(): Notification {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        var remoteViews=RemoteViews(context.packageName,R.layout.custom_layout_notification)
        remoteViews.setTextViewText(R.id.tvCases,"Nhiễm bệnh: ${data?.cases}")
        remoteViews.setTextViewText(R.id.tvDeaths,"Tử vong: ${data?.deaths}")
        remoteViews.setTextViewText(R.id.tvRecovery,"Hồi phục: ${data?.recovered}")

        remoteViews.setOnClickPendingIntent(R.id.tvTitle,PendingIntent.getBroadcast(context,1001,Intent(Constants.RECEIVER_ACTION_NAME).apply {
            putExtra("code","update")
        },PendingIntent.FLAG_UPDATE_CURRENT))

        remoteViews.setOnClickPendingIntent(R.id.imgClose,PendingIntent.getBroadcast(context,1002,Intent(Constants.RECEIVER_ACTION_NAME).apply {
            putExtra("code","close")
        },PendingIntent.FLAG_UPDATE_CURRENT))

        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
        synchronized(builder) {}
        return builder.build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "NCOVI"
            val description = "NCOVI"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )
            notificationManager.createNotificationChannel(channel)
        }
    }
}