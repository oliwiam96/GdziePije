package com.oliwia.piwo.Permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

class PermissionsGuard(private var activity : Activity, val onPermissionGrantedCallback: (Int) -> Unit): ActivityCompat.OnRequestPermissionsResultCallback{
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult, requestCode $requestCode")
        grantResults.forEach {
            if(it == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "granted permission, requestCode $requestCode")
                onPermissionGrantedCallback(requestCode)
            }
        }
    }

    fun acquirePermissions() {
        checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
    }

    fun arePermissionsGranted(permission: String) =
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(permission : String, permissionCode : Int)
    {
        if (ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(permission), permissionCode)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, arrayOf(permission),
                        permissionCode)
            }
        } else {
            onPermissionGrantedCallback(LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        val TAG = "PermissionGuard"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
