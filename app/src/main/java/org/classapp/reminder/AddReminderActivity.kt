package org.classapp.reminder

import LocationReminderService
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import org.classapp.reminder.model.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddReminderActivity : ComponentActivity() {

    val reminderRepo = ReminderRepo()
    val locationReminderRepo = LocationReminderRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderAdd()
        }
    }

    @Composable
    fun ReminderAdd() {
        var mExpanded by remember { mutableStateOf(false) }
        val reminderType = listOf("Add Reminder by Date/Time", "Add Reminder by Location")
        var SelectedText by remember { mutableStateOf("") }
        var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

        val icon = if (mExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        Column(Modifier.padding(4.dp)) {
            Column(Modifier.padding(0.dp)) {
                OutlinedTextField(
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFEE7B20)
                    ),
                    value = SelectedText,
                    onValueChange = { SelectedText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)
                        .onGloballyPositioned { coordinates ->
                            mTextFieldSize = coordinates.size.toSize()
                        },
                    label = { Text("Reminder Type", color = Color.LightGray) },
                    trailingIcon = {
                        Icon(icon, "contentDescription",
                            Modifier.clickable { mExpanded = !mExpanded })
                    }
                )

                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                ) {
                    reminderType.forEach { label ->
                        DropdownMenuItem(onClick = {
                            SelectedText = label
                            mExpanded = false
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }

            if (SelectedText == "Add Reminder by Date/Time") {
                AddReminderScreen()
            }
            if (SelectedText == "Add Reminder by Location") {
                AddReminderLocationScreen()
            }
        }
    }


    @Composable
    fun AddReminderLocationScreen() {
        var title by remember { mutableStateOf(TextFieldValue()) }
        var description by remember { mutableStateOf(TextFieldValue()) }
        var distance by remember { mutableStateOf(TextFieldValue()) }

        var search_text by remember { mutableStateOf("") }
        var search_result_list = remember { mutableStateListOf<SearchResult>() }
        var isFocus by remember { mutableStateOf(false) }

        var selected by remember { mutableStateOf<SearchResult?>(null) }

        val cameraPositionState = rememberCameraPositionState {
            position =
                CameraPosition.fromLatLngZoom(LatLng(13.867914104883598, 100.60576811948593), 17f)
        }

        val coroutineScope = rememberCoroutineScope()
        var isGranted by remember { mutableStateOf(false) }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                isGranted =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
            }
        )

        LaunchedEffect(key1 = Unit) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }

        LaunchedEffect(key1 = search_text) {
            val re = SearchAPI.service.search(search_text).data.map {
                SearchResult(name = it.name, lat = it.lat, lon = it.lon)
            }
            search_result_list.clear()
            search_result_list.addAll(re)
        }


        Column(modifier = Modifier.padding(16.dp)) {
            if (isGranted) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    GoogleMap(
                        cameraPositionState = cameraPositionState,
                        modifier = Modifier.fillMaxSize(),
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(compassEnabled = true)
                    )
                    {
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                            },
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pin),
                                contentDescription = "marker",
                            )
                        }
                    }
                    Column() {
                        OutlinedTextField(
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFEE7B20)
                            ),
                            value = search_text,
                            onValueChange = { search_text = it },
                            label = { Text("Searcn", color = Color.LightGray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .onFocusChanged { isFocus = it.isFocused }

                        )
                        if (isFocus) {
                            SearchList(search_result_list, onClick = {
                                search_text = it.name
                                selected = it
                                isFocus = false
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newCameraPosition(
                                            CameraPosition(LatLng(it.lat, it.lon), 16.0f, 0f, 0f)
                                        ),
                                        durationMs = 2000
                                    )
                                }
                            })
                        }
                    }
                }
            }

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFEE7B20)
                ),
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFEE7B20)
                ),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFEE7B20)
                ),
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEE7B20)),
                onClick = {
                    onSaveLocationReminder(
                        title = title.text,
                        desc = description.text,
                        distance = distance.text.toFloat(),
                        targetLat = selected?.lat ?: 0.0,
                        targetLon = selected?.lon ?: 0.0,
                        name = search_text
                    )

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save", color = Color.White)
            }
        }
    }

    private fun onSaveLocationReminder(
        title: String,
        desc: String,
        distance: Float,
        targetLat: Double,
        targetLon: Double,
        name: String
    ) {

        val reminder = LocationReminderRequest(
            title = title,
            desc = desc,
            distance = distance.toString(),
            targetLat = targetLat.toString(),
            targetLon = targetLon.toString(),
            name = name
        )

        locationReminderRepo.addLocationReminder(reminder) {}

        val intent = Intent()
        intent.putExtra("title", title)
        intent.putExtra("desc", desc)
        intent.putExtra("distance", distance)
        intent.putExtra("targetLat", targetLat)
        intent.putExtra("targetLon", targetLon)
        intent.putExtra("name", name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onSaveReminder(
        title: String,
        desc: String,
        date: String,
        time: String,
    ) {

        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEE, d MMM yyyy h:mm a", Locale.getDefault())
        val dateTime = LocalDateTime.parse("$date $time", formatter)
        Log.d("save", "$date $time")

        setReminder(this, title, desc, dateTime)

        reminderRepo.addReminder(
            ReminderRequest(
                title = title,
                desc = desc,
                dateTime = "$date $time"
            )
        ) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    @Composable
    fun AddReminderScreen() {
        var title by remember { mutableStateOf(TextFieldValue()) }
        var description by remember { mutableStateOf(TextFieldValue()) }
        var date by remember { mutableStateOf<String?>(null) }
        var time by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current

        Column(modifier = Modifier.padding(16.dp)) {

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFEE7B20)
                ),
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFEE7B20)
                ),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth()
            )

            val selectedDate = remember { Calendar.getInstance() }

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEE7B20)),
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                selectedDate.set(year, month, dayOfMonth)
                                val formattedDate =
                                    SimpleDateFormat("EEE, dd MMM yyyy").format(selectedDate.time)
                                date = formattedDate
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(date ?: "Set Date", color = Color.White)
                }

                Spacer(modifier = Modifier.width(6.dp))

                val selectedTime = remember { Calendar.getInstance() }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEE7B20)),
                    onClick = {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                selectedTime.set(Calendar.MINUTE, minute)
                                val formattedTime = SimpleDateFormat("hh:mm a").format(selectedTime.time)
                                time = formattedTime
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(time ?: "Set Time", color = Color.White)
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEE7B20)),
                onClick = { onSaveReminder(title.text, description.text, date ?: "", time ?: "") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save", color = Color.White)
            }
        }
    }

    @Composable
    fun DatePicker(
        onDateSelected: (String) -> Unit,
        selectedDate: Calendar,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            val year = selectedDate.get(Calendar.YEAR)
            val month = selectedDate.get(Calendar.MONTH)
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                LocalContext.current,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    val formattedDate =
                        SimpleDateFormat("EEE, dd MMM yyyy").format(selectedDate.time)
                    onDateSelected(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    @Composable
    fun TimePicker(
        onTimeSelected: (String) -> Unit,
        selectedTime: Calendar,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            val hour = selectedTime.get(Calendar.HOUR_OF_DAY)
            val minute = selectedTime.get(Calendar.MINUTE)

            TimePickerDialog(
                LocalContext.current,
                { _, hourOfDay, minute ->
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val formattedTime = SimpleDateFormat("hh:mm a").format(selectedTime.time)
                    onTimeSelected(formattedTime)
                },
                hour,
                minute,
                true
            ).show()
        }
    }
}