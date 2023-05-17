package org.classapp.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.classapp.reminder.model.Reminder
import org.classapp.reminder.model.SearchResult


@Composable
fun SearchList(searchList: List<SearchResult>, onClick:(SearchResult)-> Unit){

    LazyColumn{
        items(searchList){
            Surface(modifier = Modifier.clickable {
                onClick(it)
            }) {
                SearchCard(search = it, modifier =  Modifier.padding(1.dp))
            }

        }
    }

}