package com.example.todoapp.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.todoapp.constants.NotificationStructure.TASK_ID
import com.example.todoapp.constants.NotificationStructure.TASK_TITLE
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.Calendar

fun setAlarm(context: Context, taskId: String, date: LocalDate, time: LocalTime, title: String) {
    val calendar = Calendar.getInstance().apply {
        set(date.year, date.monthValue - 1, date.dayOfMonth, time.hour, time.minute)
    }

    val triggerTime = calendar.timeInMillis
    val currentTime = System.currentTimeMillis()
    Log.d("NotificationService", "Fecha y hora de la alarma: $triggerTime")
    if (triggerTime < currentTime) {
        // La fecha y hora están en el pasado, no programar la alarma
        Log.d("NotificationService", "La fecha y hora de la alarma están en el pasado")
        return
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(TASK_ID, taskId.hashCode())
        putExtra(TASK_TITLE, title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            Log.d("NotificationService", "Programando alarma exacta")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            Log.d("NotificationService", "Solicitando permiso para programar alarma exacta")
            // Solicitar permiso si es necesario
            val intent2 = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent2.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent2)
        }
    } else {
        Log.d("NotificationService", "Programando alarma")
        // Para versiones anteriores, se supone que se concede el permiso
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

fun cancelAlarm(context: Context, taskId: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(TASK_ID, taskId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.hashCode(),
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )

    pendingIntent?.let {
        alarmManager.cancel(it)
    }
}