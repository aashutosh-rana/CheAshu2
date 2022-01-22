package com.bcebhagalpur.cheashu.dashboard.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.helper.CurrentLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.lang.NullPointerException
import java.lang.reflect.InvocationTargetException
import java.util.*

class PgActivity : AppCompatActivity() {

    private lateinit var etOwnerName:TextInputLayout
    private lateinit var etPgName:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etCity:TextInputLayout
    private lateinit var etFullAddress:TextInputLayout
    private lateinit var etSingleRoomCount:TextInputLayout
    private lateinit var etDoubleRoomCount:TextInputLayout
    private lateinit var etTripleRoomCount:TextInputLayout
    private lateinit var etSingleRoomRent:TextInputLayout
    private lateinit var etDoubleRoomRent:TextInputLayout
    private lateinit var etTripleRoomRent:TextInputLayout
    private lateinit var etAdvanceDeposit:TextInputLayout
    private lateinit var btnNext:Button

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude:Double = 0.0
    var longitude:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pg)

        btnNext=findViewById(R.id.btnNext)
        etOwnerName=findViewById(R.id.etOwnerName)
        etPgName=findViewById(R.id.etPgName)
        etEmail=findViewById(R.id.etEmail)
        etCity=findViewById(R.id.etCity)
        etFullAddress=findViewById(R.id.etFullAddress)
        etSingleRoomCount=findViewById(R.id.etSingleBedCount)
        etDoubleRoomCount=findViewById(R.id.etDoubleBedCount)
        etTripleRoomCount=findViewById(R.id.etTripleBedCount)
        etSingleRoomRent=findViewById(R.id.etSingleBedRent)
        etDoubleRoomRent=findViewById(R.id.etDoubleBedRent)
        etTripleRoomRent=findViewById(R.id.etTripleBedRent)
        etAdvanceDeposit=findViewById(R.id.etAdvanceDeposit)

        try {
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("PgBasicInfo", Context.MODE_PRIVATE)

           val ownerName=sharedPreferences.getString("ownerName","")
           val pgName=sharedPreferences.getString("PgName","")
           val email=sharedPreferences.getString("email","")
           val cityName=sharedPreferences.getString("cityName","")
           val fullAddress=sharedPreferences.getString("fullAddress","")
           val singleRoomCount=sharedPreferences.getString("singleRoomCount","")
           val doubleRoomCount=sharedPreferences.getString("doubleRoomCount","")
           val tripleRoomCount=sharedPreferences.getString("tripleRoomCount","")
           val singleRoomRent=sharedPreferences.getString("singleRoomRent","")
           val doubleRoomRent=sharedPreferences.getString("doubleRoomRent","")
           val tripleRoomRent=sharedPreferences.getString("tripleRoomRent","")
           val advanceDeposit=sharedPreferences.getString("advanceDeposit","")

//            val lt = sharedPreferences.getString("latitude", "")
//            val lnt = sharedPreferences.getString("longitude", "")
            if (ownerName.equals("") && email.equals("") && cityName.equals("") &&
                    fullAddress.equals("")
                    && singleRoomCount.equals("") && doubleRoomCount.equals("") &&tripleRoomCount.equals("")
                    && singleRoomRent.equals("")  && doubleRoomRent.equals("") && tripleRoomRent.equals("")
                    && advanceDeposit.equals("")) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            } else {
                etOwnerName.editText!!.setText(ownerName.toString())
                etPgName.editText!!.setText(pgName.toString())
                etEmail.editText!!.setText(email.toString())
                etCity.editText!!.setText(cityName.toString())
                etFullAddress.editText!!.setText(fullAddress.toString())
                etSingleRoomCount.editText!!.setText(singleRoomCount.toString())
                etDoubleRoomCount.editText!!.setText(doubleRoomCount.toString())
                etTripleRoomCount.editText!!.setText(tripleRoomCount.toString())
                etSingleRoomRent.editText!!.setText(singleRoomRent.toString())
                etDoubleRoomRent.editText!!.setText(doubleRoomRent.toString())
                etTripleRoomRent.editText!!.setText(tripleRoomRent.toString())
                etAdvanceDeposit.editText!!.setText(advanceDeposit.toString())
            }

        } catch (e: NullPointerException) {
            Toast.makeText(this, "error 1", Toast.LENGTH_SHORT).show()
        }

        getCurrentLocation()
        displayLocationSettingsRequest(this)
        updateUi()

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
                LocationServices.getFusedLocationProviderClient(this@PgActivity).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                        latitude = p0.locations[latestLocationIndex].latitude
                        longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(this@PgActivity, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                    longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etFullAddress.editText!!.setText(fullAddress)
                            etCity.editText!!.setText(city)
                        }catch (e: IOException){
                            Toast.makeText(this@PgActivity,"Failed to get location", Toast.LENGTH_SHORT).show()
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
                        status.startResolutionForResult(this@PgActivity, REQUEST_CHECK_SETTINGS)
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


    private fun updateUi(){
        val propertyType=intent.getStringExtra("propertyType")
         val db = FirebaseFirestore.getInstance()
        val db1 = db.collection(propertyType!!).document().id
        btnNext.setOnClickListener {
            if (!TextUtils.isEmpty(etOwnerName.editText!!.text.toString()) && !TextUtils.isEmpty(etPgName.editText!!.text.toString()) &&
                    !TextUtils.isEmpty(etCity.editText!!.text.toString()) && !TextUtils.isEmpty(etFullAddress.editText!!.text.toString()) &&
                    (!TextUtils.isEmpty(etSingleRoomCount.editText!!.text.toString()) || !TextUtils.isEmpty(etDoubleRoomCount.editText!!.text.toString())
                            || !TextUtils.isEmpty(etTripleRoomCount.editText!!.text.toString())) &&
                    (!TextUtils.isEmpty(etSingleRoomRent.editText!!.text.toString()) || !TextUtils.isEmpty(etDoubleRoomRent.editText!!.text.toString())
                            || !TextUtils.isEmpty(etTripleRoomRent.editText!!.text.toString()))){

                val sharedPreferences: SharedPreferences =this.getSharedPreferences("PgBasicInfo", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor =  sharedPreferences.edit()

                editor.putString("ownerName",etOwnerName.editText!!.text.toString())
                editor.putString("PgName",etPgName.editText!!.text.toString())
                editor.putString("email",etEmail.editText!!.text.toString())
                editor.putString("cityName",etCity.editText!!.text.toString())
                editor.putString("fullAddress",etFullAddress.editText!!.text.toString())
                editor.putString("singleRoomCount",etSingleRoomCount.editText!!.text.toString())
                editor.putString("doubleRoomCount",etDoubleRoomCount.editText!!.text.toString())
                editor.putString("tripleRoomCount",etTripleRoomCount.editText!!.text.toString())
                editor.putString("singleRoomRent",etSingleRoomRent.editText!!.text.toString())
                editor.putString("doubleRoomRent",etDoubleRoomRent.editText!!.text.toString())
                editor.putString("tripleRoomRent",etTripleRoomRent.editText!!.text.toString())
                editor.putString("advanceDeposit",etAdvanceDeposit.editText!!.text.toString())
                editor.putString("db1",db1)
                editor.apply()
                editor.commit()

                                val intent= Intent(this,PgActivity2::class.java)

                intent.putExtra("ownerName",etOwnerName.editText!!.text.toString())
                intent.putExtra("PgName",etPgName.editText!!.text.toString())
                intent.putExtra("email",etEmail.editText!!.text.toString())
                intent.putExtra("cityName",etCity.editText!!.text.toString())
                intent.putExtra("fullAddress",etFullAddress.editText!!.text.toString())
                intent.putExtra("singleRoomCount",etSingleRoomCount.editText!!.text.toString())
                intent.putExtra("doubleRoomCount",etDoubleRoomCount.editText!!.text.toString())
                intent.putExtra("tripleRoomCount",etTripleRoomCount.editText!!.text.toString())
                intent.putExtra("singleRoomRent",etSingleRoomRent.editText!!.text.toString())
                intent.putExtra("doubleRoomRent",etDoubleRoomRent.editText!!.text.toString())
                intent.putExtra("tripleRoomRent",etTripleRoomRent.editText!!.text.toString())
                intent.putExtra("advanceDeposit",etAdvanceDeposit.editText!!.text.toString())
                intent.putExtra("latitude",latitude.toString())
                intent.putExtra("longitude",longitude.toString())
                intent.putExtra("propertyType",propertyType)
                intent.putExtra("db1", db1)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"please fill required filled${latitude.toString()}+${longitude.toString()}"
                        ,Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}