package com.bcebhagalpur.cheashu.dashboard.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputLayout
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

class CurrentLocation :Activity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude:Double = 0.0
    var longitude:Double=0.0

    fun getCurrentLocation(context: Context,etFullAddress:TextInputLayout,etCity:TextInputLayout){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 2000
        locationRequest.interval = 4000
        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                       val latitude = p0.locations[latestLocationIndex].latitude
                        val longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                    longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etFullAddress.editText!!.setText(fullAddress)
                            etCity.editText!!.setText(city)
                        }catch (e: IOException){
                            Toast.makeText(context,"Failed to get location", Toast.LENGTH_SHORT).show()
                        }

//                      txt_location.text=" ${address.getAddressLine(0)},${address.locality}"
                    } catch (e: InvocationTargetException) {
                        e.targetException
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    111
            )
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

        result.setResultCallback { p0 ->
            val status: Status = p0.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(TAG, "All location settings are satisfied.")
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(TAG,
                            "Location settings are not satisfied. Show the user a dialog to" +
                                    "upgrade location settings "
                    )
                    try {
                        status.startResolutionForResult(context as Activity, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                    Log.i(TAG,
                            "Location settings are inadequate, and cannot be fixed here. Dialog " +
                                    "not created."
                    )
            }
        }
    }

    companion object {
        const val TAG = "OwnerActivity"
        const val REQUEST_CHECK_SETTINGS = 0x1
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {

            if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                            this, "permission allowed",
                            Toast.LENGTH_SHORT
                    ).show()
                    fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                    )
                    return
                } else {
                    Toast.makeText(
                            this, "some error occurred",
                            Toast.LENGTH_SHORT
                    ).show()
                    return
                }

            } else {
                Toast.makeText(
                        this, "Permission Denied",
                        Toast.LENGTH_SHORT
                ).show()
                return
            }

        }

    }

}