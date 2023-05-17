package org.classapp.reminder

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.classapp.reminder.model.Reminder
import org.classapp.reminder.model.ReminderRequest
import org.classapp.reminder.model.ReminderResponse

class ReminderRepo {

    private val db = Firebase.firestore
    private val remindersCollection = db.collection("reminders")

    fun getReminders( onsuccess: (List<ReminderResponse>) -> Unit ){

        val reminders = mutableListOf<ReminderResponse>()
        remindersCollection
            .orderBy("dateTime")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    reminders.add(ReminderResponse(id = it.id, title = it.data?.get("title") as? String?: "", desc = it.data?.get("desc") as? String?: "", dateTime = it.data?.get("dateTime") as? String?: ""))
                }
                onsuccess(reminders)
            }
            .addOnFailureListener { exception ->
                onsuccess(emptyList())
            }
    }

    fun addReminder(reminder: ReminderRequest, onsuccess: () -> Unit) {
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

    fun deleteReminder(reminder: ReminderResponse, onsuccess: () -> Unit) {
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