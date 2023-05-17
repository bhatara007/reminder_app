package org.classapp.reminder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.classapp.reminder.model.LocationReminder
import org.classapp.reminder.model.LocationReminderResponse
import org.classapp.reminder.model.ReminderResponse

@Composable
fun LocationReminderCard(
    reminder: LocationReminderResponse,
    modifier: Modifier,
    onDelete: (LocationReminderResponse) -> Unit
) {

    val LocationReminderService = LocationReminderRepo()

    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.LightGray)
    ) {
        Box() {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(reminder.title, style = MaterialTheme.typography.h4)
                Text(reminder.desc, style = MaterialTheme.typography.body2)
                Text(reminder.name, style = MaterialTheme.typography.body2)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.padding(top = 20.dp, end = 8.dp),
                    onClick = {
                        onDelete(reminder)
                    },
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "marker",
                    )
                }
            }
        }
    }
}