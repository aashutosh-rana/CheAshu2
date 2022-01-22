package com.bcebhagalpur.cheashu.dashboard.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
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

class MarriageHallActivity : AppCompatActivity() {

    var suitableFor:ArrayList<String>?= arrayListOf()


    private lateinit var etHallName:TextInputLayout
    private lateinit var etOwnerName:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etCity:TextInputLayout
    private lateinit var etFullAddress:TextInputLayout
    private lateinit var etHallArea:TextInputLayout
    private lateinit var etCapacity:TextInputLayout
    private lateinit var btnNext:Button
    private lateinit var txtSuitableFor:TextView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude:Double = 0.0
    var longitude:Double=0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marriage_hall)

        etHallName=findViewById(R.id.etHallName)
        etOwnerName=findViewById(R.id.etOwnerName)
        etEmail=findViewById(R.id.etEmail)
        etCity=findViewById(R.id.etCity)
        etFullAddress=findViewById(R.id.etFullAddress)
        etHallArea=findViewById(R.id.etHallArea)
        etCapacity=findViewById(R.id.etCapacity)
        btnNext=findViewById(R.id.btnNext)
        txtSuitableFor=findViewById(R.id.txtSuitableFor)

        txtSuitableFor.setOnClickListener {
            val inflateView = LayoutInflater.from(this).inflate(R.layout.check_event_item, null)

            val weddingCeremony = inflateView.findViewById<CheckBox>(R.id.weddingCeremony)
            val weddingReception = inflateView.findViewById<CheckBox>(R.id.weddingReception)
            val birthdayParty = inflateView.findViewById<CheckBox>(R.id.birthdayParty)
            val bachelorParty = inflateView.findViewById<CheckBox>(R.id.bachelorParty)
            val cswHall = inflateView.findViewById<CheckBox>(R.id.cswHall)
            val other = inflateView.findViewById<CheckBox>(R.id.other)
            val subject = arrayOf(weddingCeremony, weddingReception, birthdayParty, bachelorParty,cswHall,other)
            val subject1 = arrayOf(
                "Wedding ceremony", "Wedding reception", "Birthday party", "Bachelor party",
                "Conference/Seminar/Workshop","Other")
            var txt1: String=""
            var txt2: String=""
            var txt3: String=""
            var txt4: String=""
            var txt5: String=""
            var txt6: String=""
            var txtList= arrayListOf(txt1,txt2,txt3,txt4,txt5,txt6)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("select events")
            alertDialog.setView(inflateView)
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Ok") { _, _ ->
                if (subject[0].isChecked) {
                    txt1 = subject1[0]
                    suitableFor!!.add(txt1)
                }else txt1 =""
                if (subject[1].isChecked) {
                    txt2 = subject1[1]
                    suitableFor!!.add(txt2)
                }else txt2 = ""
                if (subject[2].isChecked) {
                    txt3 = subject1[2]
                    suitableFor!!.add(txt3)
                }else txt3 =""
                if (subject[3].isChecked) {
                    txt4 = subject1[3]
                    suitableFor!!.add(txt4)
                }else txt4=""
                if (subject[4].isChecked) {
                    txt5 = subject1[4]
                    suitableFor!!.add(txt5)
                }else txt5=""
                if (subject[5].isChecked) {
                    txt6 = subject1[5]
                    suitableFor!!.add(txt6)
                }else txt6=""

                if (txtSuitableFor.text==""){
                    txtSuitableFor.text = "Suitable for events".trim()
                }
                txtSuitableFor.text = "$txt1 $txt2 $txt3 $txt4 $txt5 $txt6".trim()
//                for (i in txtList){
//                    if (i!=""){
//                        suitableFor!!.add(i)
//                    }
//                }
            }
            val dialog = alertDialog.create()
            dialog.show()
        }

        try {
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("HallBasicInfo", Context.MODE_PRIVATE)
            val hallName = sharedPreferences.getString("hallName", "")
            val ownerName = sharedPreferences.getString("ownerName", "")
            val email = sharedPreferences.getString("email", "")
            val city = sharedPreferences.getString("city", "")
            val fullAddress = sharedPreferences.getString("fullAddress", "")
            val hallArea = sharedPreferences.getString("hallArea", "")
            val capacity = sharedPreferences.getString("capacity", "")
//            val lt = sharedPreferences.getString("latitude", "")
//            val lnt = sharedPreferences.getString("longitude", "")
            if (hallName.equals("") && ownerName.equals("") && email.equals("") && city.equals("") &&
                fullAddress.equals("")
                && hallArea.equals("") && capacity.equals("")) {
                Toast.makeText(this, "nullabilities", Toast.LENGTH_SHORT).show()
            } else {
                etHallName.editText!!.setText(hallName.toString())
                etOwnerName.editText!!.setText(ownerName.toString())
                etEmail.editText!!.setText(email.toString())
                etCity.editText!!.setText(city.toString())
                etFullAddress.editText!!.setText(fullAddress.toString())
                etHallArea.editText!!.setText(hallArea.toString())
                etCapacity.editText!!.setText(capacity.toString())
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
                LocationServices.getFusedLocationProviderClient(this@MarriageHallActivity).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                        latitude = p0.locations[latestLocationIndex].latitude
                        longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(this@MarriageHallActivity, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etFullAddress.editText!!.setText(fullAddress)
                            etCity.editText!!.setText(city)
                        }catch (e: IOException){
                            Toast.makeText(this@MarriageHallActivity,"Failed to get location", Toast.LENGTH_SHORT).show()
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
                        status.startResolutionForResult(this@MarriageHallActivity, REQUEST_CHECK_SETTINGS)
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
            if (!TextUtils.isEmpty(etHallName.editText!!.text) && !TextUtils.isEmpty(etCity.editText!!.text) &&
                !TextUtils.isEmpty(etFullAddress.editText!!.text) && !TextUtils.isEmpty(etHallArea.editText!!.text)
                && txtSuitableFor.text!="Suitable for events" && txtSuitableFor.text!="" && !TextUtils.isEmpty(etCapacity.editText!!.text)){

                val sharedPreferences: SharedPreferences =this.getSharedPreferences("HallBasicInfo", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("hallName",etHallName.editText!!.text.toString())
                editor.putString("ownerName",etOwnerName.editText!!.text.toString())
                editor.putString("email", etEmail.editText!!.text.toString())
                editor.putString("city",etCity.editText!!.text.toString())
                editor.putString("fullAddress",etFullAddress.editText!!.text.toString())
                editor.putString("hallArea",etHallArea.editText!!.text.toString())
                editor.putString("capacity",etCapacity.editText!!.text.toString())
                editor.apply()
                editor.commit()

                val intent=Intent(this,MarriageHallActivity2::class.java)
                intent.putExtra("hallName",etHallName.editText!!.text.toString())
                intent.putExtra("ownerName",etOwnerName.editText!!.text.toString())
                intent.putExtra("email",etEmail.editText!!.text.toString())
                intent.putExtra("city",etCity.editText!!.text.toString())
                intent.putExtra("fullAddress",etFullAddress.editText!!.text.toString())
                intent.putExtra("hallArea",etHallArea.editText!!.text.toString())
                intent.putExtra("capacity",etCapacity.editText!!.text.toString())
                intent.putExtra("longitude",longitude.toString())
                intent.putExtra("latitude",latitude.toString())
                intent.putExtra("propertyType",propertyType!!.toString())
                intent.putExtra("suitableFor",txtSuitableFor.text.toString())
                intent.putExtra("suitableForArray",suitableFor)
                intent.putExtra("db1",db1)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please fill required fields",Toast.LENGTH_SHORT).show()
                txtSuitableFor.text="Suitable for events"
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}
