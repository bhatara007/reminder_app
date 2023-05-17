package org.classapp.reminder.model

data class Data(
    val address: String,
    val contributor: String,
    val icon: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val obsoleted: Boolean,
    val tag: List<String>,
    val tel: String,
    val type: String,
    val url: String,
    val verified: Boolean
)