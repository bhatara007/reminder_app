package org.classapp.reminder.model

data class LocationReminderResponse (
    val id: String,
    val name: String,
    val title: String,
    val desc: String,
    val distance: Float,
    val targetLat: Double,
    val targetLon: Double
        )