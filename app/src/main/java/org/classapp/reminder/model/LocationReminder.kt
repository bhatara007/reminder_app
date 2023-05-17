package org.classapp.reminder.model

data class LocationReminder (
        val title: String,
        val desc: String,
        val distance: Float,
        val targetLat: Double,
        val targetLon: Double
)