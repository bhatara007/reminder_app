package org.classapp.reminder

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.classapp.reminder.model.*

class LocationReminderRepo {

    private val db = Firebase.firestore
    private val remindersCollection = db.collection("location_reminders")

    fun getLocationReminders(onsuccess: (List<LocationReminderResponse>) -> Unit) {

        val reminders = mutableListOf<LocationReminderResponse>()
        var distance: String
        var targetLat: String
        var targetLon: String

        remindersCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    distance = it.data?.get("distance") as String
                    targetLat = it.data?.get("targetLat") as String
                    targetLon = it.data?.get("targetLon") as String
                    reminders.add(
                        LocationReminderResponse(
                            id = it.id,
                            title = it.data?.get("title") as? String ?: "",
                            desc = it.data?.get("desc") as? String ?: "",
                            name = it.data?.get("name") as? String ?: "",
                            distance = distance.toFloat(),
                            targetLat = targetLat.toDouble(),
                            targetLon = targetLon.toDouble(),
                        )
                    )
                }
                onsuccess(reminders)
            }
            .addOnFailureListener { exception ->
                onsuccess(emptyList())
            }
    }

    fun addLocationReminder(reminder: LocationReminderRequest, onsuccess: () -> Unit) {
        remindersCollection
            .add(reminder)
            .addOnSuccessListener {
                Log.d(TAG, "Reminder added with ID: ${it.id}")
                onsuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding reminder: ", exception)
            }
    }

    fun deleteLocationReminder(reminder: LocationReminderResponse, onsuccess: () -> Unit) {
        val reminderId = reminder.id
        remindersCollection
            .document(reminderId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Reminder updated with ID: $reminderId")
                onsuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating reminder: ", exception)
            }
    }

}