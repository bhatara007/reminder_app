package org.classapp.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.classapp.reminder.model.LocationReminderResponse
import org.classapp.reminder.model.Reminder
import org.classapp.reminder.model.ReminderResponse


@Composable
fun ReminderList(
    reminderList: List<ReminderResponse>,
    locationReminderList: List<LocationReminderResponse>,
    onReminderDelete: (ReminderResponse) -> Unit,
    onLocationReminderDelete: (LocationReminderResponse) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(reminderList) {
            Surface(modifier = Modifier.clickable {
            }) {
                ReminderCard(reminder = it, modifier = Modifier, onDelete = onReminderDelete)
            }

        }
        items(locationReminderList) {
            Surface(modifier = Modifier.clickable {
            }) {
                LocationReminderCard(
                    reminder = it,
                    modifier = Modifier,
                    onDelete = onLocationReminderDelete
                )
            }

        }

    }

}