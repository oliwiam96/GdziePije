package com.oliwia.piwo.LocalisationService

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.oliwia.piwo.MainActivity
import com.oliwia.piwo.Permissions.PermissionsGuard
import java.util.*

class GPSManager(val newPositionCallback: (Location) -> Unit, private val permissionGuard: PermissionsGuard) {
    @SuppressLint("MissingPermission")
    fun getPosition(mapsActivity: MainActivity) {
        if(!permissionGuard.arePermissionsGranted(android.Manifest.permission.ACCESS_FINE_LOCATION))
        {
            return
        }

        val locationManager = mapsActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location?) {
                location?.let {
                    Log.i(TAG, "Changed position $location")
                    newPositionCallback(Location(it.latitude, it.longitude, Date()))
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

    val TAG = "GPSManager"

    companion object {
        const val locationProvider = LocationManager.GPS_PROVIDER
    }
}