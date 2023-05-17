import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.layout.ColumnScope
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.classapp.reminder.model.LocationReminder
import org.classapp.reminder.model.LocationReminderRequest
import org.classapp.reminder.setReminder
import java.time.LocalDateTime

class LocationReminderService(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var reminderList: MutableList<LocationReminderRequest> = mutableListOf()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val location = locationResult.lastLocation
            checkReminders(location)
        }
    }

    fun addLocationReminder(reminder: LocationReminderRequest){
        reminderList.add(reminder)
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun start() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkReminders(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            for (reminder in reminderList) {
                val targetLocation = Location("").apply {
                    latitude = reminder.targetLat.toDouble()
                    longitude = reminder.targetLon.toDouble()
                }
                val distance = location.distanceTo(targetLocation)
                if (distance < reminder.distance.toFloat()) {
                    setReminder(context, reminder.title, reminder.desc, dateTime = LocalDateTime.now())
                    reminderList.removeAll { it.targetLon == reminder.targetLon && it.targetLat == reminder.targetLat}
                }
            }
        }
    }
}
