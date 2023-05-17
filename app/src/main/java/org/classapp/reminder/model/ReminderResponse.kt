package org.classapp.reminder.model

import java.time.LocalDateTime

data class ReminderResponse(
    val id: String,
    val title: String,
    val desc: String,
    val dateTime: String
)