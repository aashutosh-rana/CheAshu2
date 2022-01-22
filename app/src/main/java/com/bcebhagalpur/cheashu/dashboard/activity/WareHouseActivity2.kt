package com.bcebhagalpur.cheashu.dashboard.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import com.bcebhagalpur.cheashu.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WareHouseActivity2 : AppCompatActivity() {

    private lateinit var status: String
    private lateinit var etDistance:TextInputLayout
    private lateinit var etDescription:TextInputLayout
    private lateinit var chipAvailability:Chip
    private lateinit var etSelectAvailability:EditText
    private lateinit var chipFurnishing:ChipGroup
    private lateinit var chipAmenities:ChipGroup
    private lateinit var chipSuitableFor:ChipGroup
    private lateinit var etFloorStatus:EditText
    private val db = FirebaseFirestore.getInstance()
    private val myCalendar = Calendar.getInstance()
    private var availability:String?=""
    private var propertySubType:ArrayList<String>?= arrayListOf()
    private var amenitiesList:ArrayList<String>?= arrayListOf()
    private var floor:ArrayList<String>?= arrayListOf()
    private var furnishing:ArrayList<String>?= arrayListOf()

    private var propertySubType1:ArrayList<String>?=null
    private lateinit var btnUploadProperty:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_house2)

        etDistance=findViewById(R.id.etDistance)
        etDescription=findViewById(R.id.etDescription)
        chipAvailability=findViewById(R.id.chipAvailability)
        etSelectAvailability=findViewById(R.id.etSelectAvailability)
        chipFurnishing=findViewById(R.id.chipFurnishing)

        etFloorStatus=findViewById(R.id.etFloorStatus)
        chipAmenities=findViewById(R.id.chipAmenities)
        chipSuitableFor=findViewById(R.id.chipSuitableFor)
        btnUploadProperty=findViewById(R.id.btnUpload)
        status=""


        val ownerName=intent.getStringExtra("ownerName")
        val email=intent.getStringExtra("email")
        val city=intent.getStringExtra("city")
        val fullAddress=intent.getStringExtra("fullAddress")
        val wareHouseArea=intent.getStringExtra("wareHouseArea")
        val rentPerSqft=intent.getStringExtra("rentPerSqft")
        val totalRent=intent.getStringExtra("totalRent")
        val securityMoney=intent.getStringExtra("securityMoney")
        val maintenanceCharge=intent.getStringExtra("maintenanceCharge")
        val longitude=intent.getStringExtra("longitude")
        val latitude=intent.getStringExtra("latitude")
        val propertyType=intent.getStringExtra("propertyType")
        val db1=intent.getStringExtra("db1")

        val time = Calendar.getInstance().time.toString()
        val number = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val path = "Property/$propertyType/$currentUser/$db1"
        val rootPath = "All/$db1"

        val data = UploadMarriageInfo(
                ownerName,
                email,
                city,
                fullAddress,
                latitude,
                longitude,
                propertyType,
                wareHouseArea,
                time,
                currentUser,
                number,
                path,
                rootPath,
                null,
                null,
                securityMoney,
                etDescription.editText!!.text.toString(),
                rentPerSqft,
                maintenanceCharge,
                totalRent,
                etDistance.editText!!.text.toString(),
                availability,null,null,null,null
        )
        db.document(path).set(data)
        db.document(rootPath).set(data)

        //

        etFloorStatus.setOnClickListener {
            val subject = arrayOf(
                "Ground", "1st","Top","2nd","3rd","4th","5th","6th","7th","8th","9th","10th","11th","12th","13",
                "14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30+"
            )

            val mAlertDialogBuilder =
                android.app.AlertDialog.Builder(this)
            mAlertDialogBuilder.setTitle("Select class")
            mAlertDialogBuilder.setItems(subject) { _, which ->
                when (which) {
                    which -> {
                        etFloorStatus.setText(subject[which])
                    }
                }
            }
            val mAlertDialog = mAlertDialogBuilder.create()
            mAlertDialog.show()
        }

        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel(path,rootPath)
            }

        chipAvailability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etSelectAvailability.visibility=View.GONE
                availability=chipAvailability.text.toString()
                db.document(path)
                        .update("availability", availability)
                db.document(rootPath)
                        .update("availability", availability)
            }else{
                etSelectAvailability.visibility=View.VISIBLE
                availability=etSelectAvailability.text.toString()
            }
        }

        etSelectAvailability.setOnClickListener {
            if (chipAvailability.isChecked){
               chipAvailability.isChecked=false
            }
                DatePickerDialog(
                    this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()

        }
            tags(chipAmenities, path, rootPath,"amenities", amenitiesList!!)
            tags(chipSuitableFor,path,rootPath,"suitableFor",propertySubType!!)
            tagss(chipFurnishing,path,rootPath,"furnishingStatus")


        btnUploadProperty.setOnClickListener {
            if (!TextUtils.isEmpty(etDistance.editText!!.text.toString()) && propertySubType!!.size>0 &&
                !TextUtils.isEmpty(etFloorStatus.text.toString()) && furnishing!=null && amenitiesList!=null
                && status!="") {
                db.document(path)
                        .update("distanceFromRoad", etDistance.editText!!.text.toString())
                db.document(rootPath)
                        .update("distanceFromRoad", etDistance.editText!!.text.toString())
                db.document(path)
                        .update("description", etDescription.editText!!.text.toString())
                db.document(rootPath)
                        .update("description", etDescription.editText!!.text.toString())
                db.document(path)
                    .update("floorStatus", etDescription.editText!!.text.toString())
                db.document(rootPath)
                    .update("floorStatus", etDescription.editText!!.text.toString())
                val intent = Intent(this, OwnerActivity4::class.java)
                intent.putExtra("path", path)
                intent.putExtra("rootPath", rootPath)
                intent.putExtra("propertyType", propertyType)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun tags(chipGroup: ChipGroup, path: String, rootPath: String,field:String,propertySubType2:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener {_, isChecked ->
                    if (isChecked) {
                        propertySubType2.add(it.text.toString())
                        db.document(path)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                        db.document(rootPath)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                    } else {
                        db.document(rootPath)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                        db.document(path)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    private fun tagss(chipGroup: ChipGroup, path: String, rootPath: String, field:String) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener {_, isChecked ->
                    if (isChecked) {
                        status=it.text.toString()
                        db.document(path)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                        db.document(rootPath)
                            .update(field, FieldValue.arrayUnion(it.text.toString()))
                    } else {
                        db.document(rootPath)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                        db.document(path)
                            .update(field, FieldValue.arrayRemove(it.text.toString()))
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

     private fun updateLabel(path: String, rootPath: String) {
        val myFormat = "MM/dd/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etSelectAvailability.setText(sdf.format(myCalendar.time))
        availability= myCalendar.time.toString()
        db.document(path)
                .update("availability", availability)
        db.document(rootPath)
                .update("availability", availability)
    }

    data class UploadMarriageInfo(
        val ownerName: String?,
        val email: String?,
        val city: String?,
        val fullAddress: String?,
        val latitude: String?,
        val longitude: String?,
        val propertyType: String?,
        val wareHouseArea: String?,
        val time: String?,
        val currentUser: String?,
        val number: String?,
        val path: String,
        val rootPath: String,
        val images: ArrayList<String>?,
        val tags: ArrayList<String>?,
        val SecurityMoney: String?,
        val description: String?,
        val rentPerSqft: String?,
        val maintenanceCharge: String?,
        val totalRent: String?,
        val distanceFromRoad: String?,
        val availability: String?,
        val amenities:ArrayList<String>?,
        val floorStatus:String?,
        val furnishingStatus:String?,
        val suitableFor:ArrayList<String>?
    )

}