package org.classapp.reminder

import LocationReminderService
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.classapp.reminder.model.*
import org.classapp.reminder.ui.theme.ReminderTheme
import java.time.LocalDateTime
import java.util.*

class MainActivity : ComponentActivity() {

    val reminderRepo = ReminderRepo()
    val locationReminderRepo = LocationReminderRepo()

    private lateinit var locationReminderService: LocationReminderService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationReminderService = LocationReminderService(this)
        locationReminderService.start()

        setContent {
            ReminderOverview(reminderRepo, locationReminderService, locationReminderRepo)
        }
    }
}


@Composable
fun ReminderOverview(
    reminderRepo: ReminderRepo,
    locationReminderService: LocationReminderService,
    locationReminderRepo: LocationReminderRepo
) {

    var reminder_list = remember { mutableStateListOf<ReminderResponse>() }
    var locationReminderList = remember { mutableStateListOf<LocationReminderResponse>() }
    var Isdelete = remember { mutableStateOf(false) }

    fun fetchReminder(){
        reminderRepo.getReminders {
            reminder_list.clear()
            reminder_list.addAll(it)
        }
        locationReminderRepo.getLocationReminders {
            locationReminderList.clear()
            locationReminderList.addAll(it)
        }
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val title = intent.getStringExtra("title") ?: ""
                    val desc = intent.getStringExtra("desc") ?: ""
                    val distance = intent.getFloatExtra("distance", 0.0f)
                    val targetLat = intent.getDoubleExtra("targetLat", 0.0)
                    val targetLon = intent.getDoubleExtra("targetLon", 0.0)
                    val name = intent.getStringExtra("name") ?: ""

                    val reminder = LocationReminderRequest(
                        title = title,
                        desc = desc,
                        distance = distance.toString(),
                        targetLat = targetLat.toString(),
                        targetLon = targetLon.toString(),
                        name = name
                    )
                    locationReminderService.addLocationReminder(reminder)
                }
                fetchReminder()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        fetchReminder()
    }

    Surface(modifier = Modifier.fillMaxSize()) {

        val context = LocalContext.current

        Column(
        ) {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text(text = "Reminder App", color = Color.White)
                }, backgroundColor = Color(0xFFEE7B20),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ArrowBack, "backIcon", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {

                        activityLauncher.launch(Intent(context, AddReminderActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Add, "addIcon", tint = Color.White)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
            ) {
                ReminderList(
                    reminder_list, locationReminderList,
                    onReminderDelete = {
                        reminderRepo.deleteReminder(it){
                            fetchReminder()
                        }
                    },
                    onLocationReminderDelete = {
                        locationReminderRepo.deleteLocationReminder(it){
                            fetchReminder()
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
        Greeting("Android")
    }
}