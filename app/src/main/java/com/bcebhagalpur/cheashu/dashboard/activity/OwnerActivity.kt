@file:Suppress("DEPRECATION")
package com.bcebhagalpur.cheashu.dashboard.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bcebhagalpur.cheashu.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OwnerActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

//    https://api.postalpincode.in/pincode/{PINCODE}

    private lateinit var etOwnerName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFullAddress: EditText
    private lateinit var etCity: EditText
    private lateinit var etMonthlyRent: EditText
    private lateinit var etMaintenanceCharge: EditText
    private lateinit var etSecurityMoney: EditText
    private lateinit var etBuildingName: EditText
    private lateinit var btnProceed: Button

    var latitude:Double = 0.0
    var longitude:Double=0.0
//    private var latitude: Double? = null
//    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)

        displayLocationSettingsRequest(this)

        etOwnerName = findViewById(R.id.etOwnerName)
        etEmail = findViewById(R.id.etEmail)
        etFullAddress = findViewById(R.id.etFullAddress)
        etCity = findViewById(R.id.etCity)
        etMonthlyRent=findViewById(R.id.etMonthlyRent)
        etMaintenanceCharge=findViewById(R.id.etMaintenanceCharge)
        etSecurityMoney=findViewById(R.id.etSecurityMoney)
        etBuildingName=findViewById(R.id.etBuildingName)
        btnProceed = findViewById(R.id.btnProceed)

        getCurrentLocation()
        updateUi()
//
//        try {
//            val sharedPreferences: SharedPreferences = this.getSharedPreferences("OwnerBasicInfo", Context.MODE_PRIVATE)
//            val nm = sharedPreferences.getString("name", "")
//            val em = sharedPreferences.getString("email", "")
//            val ad = sharedPreferences.getString("fullAddress", "")
//            val ct = sharedPreferences.getString("city", "")
//            val description=sharedPreferences.getString("description","")
////            val lt = sharedPreferences.getString("latitude", "")
////            val lnt = sharedPreferences.getString("longitude", "")
//            if (nm.equals("") && em.equals("") &&  ad.equals("")
//                    && ct.equals("") && description.equals("")){
//                Toast.makeText(this, "nullabilities", Toast.LENGTH_SHORT).show()
//            } else {
//                etOwnerName.setText(nm).toString()
//                etEmail.setText(em).toString()
//                etFullAddress.setText(ad).toString()
//                etCity.setText(ct).toString()
//                etDescription.setText(description.toString())
//            }
//
//        } catch (e: NullPointerException) {
//            Toast.makeText(this, "error 1", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun getCurrentLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 2000
        locationRequest.interval = 4000
        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                LocationServices.getFusedLocationProviderClient(this@OwnerActivity).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                         latitude = p0.locations[latestLocationIndex].latitude
                         longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(this@OwnerActivity, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                    longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etFullAddress.setText(fullAddress)
                            etCity.setText(city)
                        }catch (e:IOException){
                            Toast.makeText(this@OwnerActivity,"Failed to get location",Toast.LENGTH_SHORT).show()
                        }

//                      txt_location.text=" ${address.getAddressLine(0)},${address.locality}"
                    } catch (e: InvocationTargetException) {
                        e.targetException
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
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

    private fun displayLocationSettingsRequest(context: Context) {
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
                        status.startResolutionForResult(this@OwnerActivity, REQUEST_CHECK_SETTINGS)
                    } catch (e: SendIntentException) {
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

    private fun updateUi() {

        val propertyType=intent.getStringExtra("propertyType")
        val db = FirebaseFirestore.getInstance()
        val db1 = db.collection(propertyType!!).document().id

        btnProceed.setOnClickListener {
            if (etOwnerName.text.toString().isNotEmpty() && etEmail.text.toString().isNotEmpty() && etMonthlyRent.text.toString().isNotEmpty()
                    && etFullAddress.text.toString().isNotEmpty() && etCity.text.toString().isNotEmpty()){
                Toast.makeText(this,propertyType!!.toString()+latitude.toString()+
                    longitude.toString(),Toast.LENGTH_SHORT).show()
//                val sharedPreferences: SharedPreferences =this.getSharedPreferences("OwnerBasicInfo", Context.MODE_PRIVATE)
//                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//                editor.putString("description",etDescription.text.toString())
//                editor.putString("name", etOwnerName.text.toString())
//                editor.putString("email",etEmail.text.toString())
//                editor.putString("fullAddress",etFullAddress.text.toString())
//                editor.putString("city",etCity.text.toString())
//                editor.apply()
//                editor.commit()

                val intent=Intent(this,OwnerActivity2::class.java)
                intent.putExtra("name",etOwnerName.text.toString())
                intent.putExtra("email",etEmail.text.toString())
                intent.putExtra("address",etFullAddress.text.toString())
                intent.putExtra("city",etCity.text.toString())
                intent.putExtra("buildingName",etBuildingName.text.toString())
                intent.putExtra("latitude",latitude.toString())
                intent.putExtra("longitude",longitude.toString())
                intent.putExtra("propertyType",propertyType)
                intent.putExtra("monthlyRent",etMonthlyRent.text.toString())
                intent.putExtra("maintenanceCharge",etMaintenanceCharge.text.toString())
                intent.putExtra("securityMoney",etSecurityMoney.text.toString())
                intent.putExtra("db1",db1)
                startActivity(intent)
            }else{
                Toast.makeText(this,"null2",Toast.LENGTH_SHORT).show()
            }
        }
    }

     override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}

