package org.classapp.reminder.model

data class ReminderRequest(
    val title: String,
    val desc: String,
    val dateTime: String
)