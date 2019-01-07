package com.oliwia.piwo

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

class MapsHandler(private val onMapsReadyCallback: ()-> Unit) : OnMapReadyCallback {
    private lateinit var map: GoogleMap

    fun setOnMarkerClickListener(callback: (Marker) -> Boolean)
    {
        map.setOnMarkerClickListener { x -> callback.invoke(x)}
    }

    @SuppressLint("MissingPermission")
    fun setMyLocationEnabled(enable: Boolean)
    {
        Log.i(TAG, "setMyLocationEnabled $enable")
        map.setMyLocationEnabled(enable)
    }

    fun addMarker(marker: MarkerOptions)
    {
        map.addMarker(marker)
    }

    fun addPolyline(polylineOptions: PolylineOptions) =
            map.addPolyline(polylineOptions)

    fun moveCameraOnPosition(location: LatLng, zoomLevel: Float) {
        Log.i(TAG, "Moving camera position to $location")
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let{
            this.map = it
            setMyLocationEnabled(true)
            onMapsReadyCallback.invoke()
        }
    }

    companion object {
        val TAG="MapsHanlder"
    }
}