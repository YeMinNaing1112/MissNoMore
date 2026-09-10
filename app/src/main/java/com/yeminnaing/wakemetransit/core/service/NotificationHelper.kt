package com.yeminnaing.wakemetransit.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.yeminnaing.wakemetransit.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        const val NOTIFICATION_ID = 1
    }

    private var mediaPlayer: MediaPlayer? = null
    private val channelId = "Alarm"

    @Inject
    lateinit var alarmStateHolder: AlarmStateHolder

    init {
        createChannel()
    }

    enum class AlarmStage(
        val title: String,
        val message: String,
        val vibrate: Boolean,
        val playSound: Boolean,
        val ongoing: Boolean,
    ) {
        FAR_WARNING(
            title = "Heads up",
            message = "Your stop is in 300m",
            vibrate = false,
            playSound = false,
            ongoing = false
        ),
        NEAR_WARNING(
            title = "Get ready",
            message = "Your stop is in 200m",
            vibrate = true,
            playSound = false,
            ongoing = false
        ),
        ARRIVED(
            title = "Wake up!",
            message = "This is your stop",
            vibrate = true,
            playSound = true,
            ongoing = true
        )
    }

    private fun createChannel() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { enableVibration(true) }
        manager.createNotificationChannel(channel)
    }


    private fun playSong() {
        stopSong()
        mediaPlayer = MediaPlayer.create(context, R.raw.musical_alarm)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopSong() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    fun notifyStage(stage: AlarmStage) {
        if (stage.vibrate) vibrate()
        if (stage.playSound) playSong()
        showNotification(stage)
        if (stage == AlarmStage.ARRIVED) alarmStateHolder.trigger()
    }

    private fun vibrate() {
        val pattern = longArrayOf(0, 400, 200, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }


    private fun showNotification(stage: AlarmStage) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(stage.title)
            .setContentText(stage.message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(!stage.ongoing)
            .setOngoing(stage.ongoing)

        if (stage == AlarmStage.ARRIVED) {
            builder.addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
        }

        manager.notify(NOTIFICATION_ID, builder.build())
    }

    fun stopAlarm() {
        stopSong()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }

}