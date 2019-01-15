package com.oliwia.piwo

import android.content.Context
import android.widget.Toast

abstract class NotificationManager(val context: Context) : INotificationDisplay

class ToastNotificator(context: Context) : NotificationManager(context)
{
    override fun displayNotification(message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }
}

interface INotificationDisplay
{
    fun displayNotification(message: String, duration: Int)
}