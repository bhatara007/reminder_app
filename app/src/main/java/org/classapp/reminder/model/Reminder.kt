package org.classapp.reminder.model

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Reminder(
    val title: String,
    val desc: String,
    val dateTime: LocalDateTime
)
