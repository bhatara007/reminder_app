package org.classapp.reminder

import org.classapp.reminder.model.Reminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.classapp.reminder.model.SearchResult


@Composable
fun SearchCard(search: SearchResult, modifier: Modifier) {
    Surface() {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(search.name, style = MaterialTheme.typography.body1)
        }
    }
}