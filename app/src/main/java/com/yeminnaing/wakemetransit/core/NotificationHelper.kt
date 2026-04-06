package com.yeminnaing.wakemetransit.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import com.yeminnaing.wakemetransit.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var mediaPlayer: MediaPlayer? = null

    fun showAlarm() {
        playSong()
        showNotification()
    }

    private fun playSong() {
        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.musical_alarm
        )

        mediaPlayer?.start()
    }

    private fun showNotification() {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val channelId = "Alarm"

        val channel = NotificationChannel(
            channelId,
            "Alarm",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Wake Up")
            .setContentText("You are going to reach your stop")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()


        manager.notify(1, notification)
    }

    fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}