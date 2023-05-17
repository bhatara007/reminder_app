package org.classapp.reminder.model

data class LocationReminderRequest(
    val title: String,
    val desc: String,
    val distance: String,
    val targetLat: String,
    val targetLon: String,
    val name: String
)