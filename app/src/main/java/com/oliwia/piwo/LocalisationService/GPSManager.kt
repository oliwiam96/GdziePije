package com.oliwia.piwo.LocalisationService

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.oliwia.piwo.MainActivity
import java.util.*

class GPSManager(val newPositionCallback: (Location) -> Unit) {
    @SuppressLint("MissingPermission")
    fun getPosition(mapsActivity: MainActivity) {
        // Acquire a reference to the system Location Manager
        val locationManager = mapsActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location?) {

                location?.let {
                    Log.i("FirebaseLocator", "Changed position")
                    newPositionCallback(Location(it.longitude, it.latitude, Date()))
                }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onProviderDisabled(p0: String?) {
            }
        }

        locationManager.requestLocationUpdates(locationProvider, 10000, 50f, locationListener)
    }

    companion object {
        const val locationProvider = LocationManager.GPS_PROVIDER
    }
}