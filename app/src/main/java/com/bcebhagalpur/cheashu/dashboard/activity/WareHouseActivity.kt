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
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bcebhagalpur.cheashu.R
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

class WareHouseActivity : AppCompatActivity() {

    private lateinit var etOwnerName:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etCity:TextInputLayout
    private lateinit var etFullAddress:TextInputLayout
    private lateinit var etWareHouseArea:TextInputLayout
    private lateinit var etRentPerSqft:TextInputLayout
    private lateinit var etSecurityMoney:TextInputLayout
    private lateinit var etMaintenanceCharge:TextInputLayout
    private lateinit var etTotalRent:TextInputLayout
    private lateinit var btnNext:Button

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude:Double = 0.0
    var longitude:Double=0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_house)

        etOwnerName=findViewById(R.id.etOwnerName)
        etEmail=findViewById(R.id.etEmail)
        etCity=findViewById(R.id.etCity)
        etFullAddress=findViewById(R.id.etFullAddress)
        etWareHouseArea=findViewById(R.id.etWareHouseArea)
        etRentPerSqft=findViewById(R.id.etRentPerSqft)
        etSecurityMoney=findViewById(R.id.etSecurityMoney)
        etMaintenanceCharge=findViewById(R.id.etMaintenanceCharge)
        etTotalRent=findViewById(R.id.etTotalRent)
        btnNext=findViewById(R.id.btnNext)

        try {
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("WareHouseBasicInfo", Context.MODE_PRIVATE)

            val ownerName = sharedPreferences.getString("ownerName", "")
            val email = sharedPreferences.getString("email", "")
            val city = sharedPreferences.getString("city", "")
            val fullAddress = sharedPreferences.getString("fullAddress", "")
            val wareHouseArea = sharedPreferences.getString("wareHouseArea", "")
            val rentPerSqft = sharedPreferences.getString("rentPerSqft", "")
            val totalRent = sharedPreferences.getString("totalRent", "")
            val securityMoney = sharedPreferences.getString("securityMoney", "")
            val maintenanceCharge = sharedPreferences.getString("wareHouseArea", "")

//            val lt = sharedPreferences.getString("latitude", "")
//            val lnt = sharedPreferences.getString("longitude", "")
            if (ownerName.equals("") && email.equals("") && city.equals("") &&
                fullAddress.equals("")
                && wareHouseArea.equals("") && rentPerSqft.equals("") &&totalRent.equals("")
                && securityMoney.equals("")  && maintenanceCharge.equals("") ) {
                Toast.makeText(this, "nullabilities", Toast.LENGTH_SHORT).show()
            } else {
                etOwnerName.editText!!.setText(ownerName.toString())
                etEmail.editText!!.setText(email.toString())
                etCity.editText!!.setText(city.toString())
                etFullAddress.editText!!.setText(fullAddress.toString())
                etWareHouseArea.editText!!.setText(wareHouseArea.toString())
                etRentPerSqft.editText!!.setText(rentPerSqft.toString())
                etTotalRent.editText!!.setText(totalRent.toString())
                etSecurityMoney.editText!!.setText(securityMoney.toString())
                etMaintenanceCharge.editText!!.setText(maintenanceCharge.toString())
            }

        } catch (e: NullPointerException) {
            Toast.makeText(this, "error 1", Toast.LENGTH_SHORT).show()
        }

        displayLocationSettingsRequest(this)
        getCurrentLocation()
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
                LocationServices.getFusedLocationProviderClient(this@WareHouseActivity).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                        latitude = p0.locations[latestLocationIndex].latitude
                        longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(this@WareHouseActivity, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etFullAddress.editText!!.setText(fullAddress)
                            etCity.editText!!.setText(city)
                        }catch (e: IOException){
                            Toast.makeText(this@WareHouseActivity,"Failed to get location", Toast.LENGTH_SHORT).show()
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
                        status.startResolutionForResult(this@WareHouseActivity, REQUEST_CHECK_SETTINGS)
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
            if (!TextUtils.isEmpty(etOwnerName.editText!!.text) && !TextUtils.isEmpty(etCity.editText!!.text) &&
                !TextUtils.isEmpty(etFullAddress.editText!!.text) && !TextUtils.isEmpty(etWareHouseArea.editText!!.text)
                && !TextUtils.isEmpty(etRentPerSqft.editText!!.text)){

                val sharedPreferences: SharedPreferences =this.getSharedPreferences("WareHouseBasicInfo", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor =  sharedPreferences.edit()

                editor.putString("ownerName",etOwnerName.editText!!.text.toString())
                editor.putString("email", etEmail.editText!!.text.toString())
                editor.putString("city",etCity.editText!!.text.toString())
                editor.putString("fullAddress",etFullAddress.editText!!.text.toString())
                editor.putString("wareHouseArea",etWareHouseArea.editText!!.text.toString())
                editor.putString("rentPerSqft",etRentPerSqft.editText!!.text.toString())
                editor.putString("totalRent",etTotalRent.editText!!.text.toString())
                editor.putString("securityMoney",etSecurityMoney.editText!!.text.toString())
                editor.putString("maintenanceCharge",etMaintenanceCharge.editText!!.text.toString())
                editor.apply()
                editor.commit()

                val intent= Intent(this,WareHouseActivity2::class.java)
                intent.putExtra("ownerName",etOwnerName.editText!!.text.toString())
                intent.putExtra("email",etEmail.editText!!.text.toString())
                intent.putExtra("city",etCity.editText!!.text.toString())
                intent.putExtra("fullAddress",etFullAddress.editText!!.text.toString())
                intent.putExtra("wareHouseArea",etWareHouseArea.editText!!.text.toString())
                intent.putExtra("rentPerSqft",etRentPerSqft.editText!!.text.toString())
                intent.putExtra("totalRent",etTotalRent.editText!!.text.toString())
                intent.putExtra("securityMoney",etSecurityMoney.editText!!.text.toString())
                intent.putExtra("maintenanceCharge",etMaintenanceCharge.editText!!.text.toString())
                intent.putExtra("longitude",longitude.toString())
                intent.putExtra("latitude",latitude.toString())
                intent.putExtra("propertyType", propertyType.toString())
                intent.putExtra("db1",db1)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}
