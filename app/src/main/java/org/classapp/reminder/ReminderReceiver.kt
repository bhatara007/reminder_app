package org.classapp.reminder

import android.app.*
import android.content.*
import android.os.Build
import androidx.annotation.RequiresApi
import org.classapp.reminder.R
import java.time.*


fun setReminder(context: Context, title: String, desc: String, dateTime: LocalDateTime) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java)
    intent.putExtra("title", title)
    intent.putExtra("desc", desc)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    // set the alarm to go off at the specified datetime
    val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")

        // create a notification to display the reminder
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ReminderChannel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Reminder Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        // display the notification
        notificationManager.notify(0, notification)
    }
}
