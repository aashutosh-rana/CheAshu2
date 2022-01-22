package com.bcebhagalpur.cheashu.dashboard.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import com.bcebhagalpur.cheashu.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VillaActivity2 : AppCompatActivity() {

    private lateinit var chipBedroom:ChipGroup
    private lateinit var chipHall:ChipGroup
    private lateinit var chipPujaRoom:ChipGroup
    private lateinit var chipBathroom:ChipGroup
    private lateinit var chipTenants:ChipGroup
    private lateinit var chipFurnishing:ChipGroup
    private lateinit var chipAmenities:ChipGroup
    private lateinit var chipAvailability:Chip
    private lateinit var etSelectAvailability:EditText
    private lateinit var btnUpload:Button
    private val db = FirebaseFirestore.getInstance()
    private val myCalendar = Calendar.getInstance()
    private var availability:String?=""
    private var amenitiesList:ArrayList<String>?= arrayListOf()
    private var bathroomCount:ArrayList<String>?= arrayListOf()
    private var pujaRoomCount:ArrayList<String>?= arrayListOf()
    private var hallCount:ArrayList<String>?= arrayListOf()
    private var bedRoomCount:ArrayList<String>?= arrayListOf()
    private var tenants:ArrayList<String>?= arrayListOf()
    private var furnishingStatus:ArrayList<String>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_villa2)

        chipBedroom=findViewById(R.id.chipBedRoom)
        chipHall=findViewById(R.id.chipHall)
        chipPujaRoom=findViewById(R.id.chipPujaRoomcount)
        chipBathroom=findViewById(R.id.chipBathroom)
        chipTenants=findViewById(R.id.chipTenants)
        chipFurnishing=findViewById(R.id.chipFurnishing)
        chipAmenities=findViewById(R.id.chipAmenities)
        chipAvailability=findViewById(R.id.chipAvailability)
        etSelectAvailability=findViewById(R.id.etSelectAvailability)
        btnUpload=findViewById(R.id.btnUpload)


        val ownerName=intent.getStringExtra("ownerName")
        val email=intent.getStringExtra("email")
        val city=intent.getStringExtra("city")
        val fullAddress=intent.getStringExtra("fullAddress")
        val villaHouseArea=intent.getStringExtra("villaHouseArea")
        val monthlyRent=intent.getStringExtra("monthlyRent")
        val securityMoney=intent.getStringExtra("securityMoney")
        val maintenanceCharge=intent.getStringExtra("maintenanceCharge")
        val description=intent.getStringExtra("description")
        val longitude=intent.getStringExtra("longitude")
        val latitude=intent.getStringExtra("latitude")
        val propertyType=intent.getStringExtra("propertyType")
        val db1=intent.getStringExtra("db1")

        val time = Calendar.getInstance().time.toString()
        val number = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val path = "$propertyType/$currentUser/data/$db1"
        val rootPath = "All/$db1"

        val data=UploadVillaInfo(ownerName,email,city,fullAddress,latitude,longitude,propertyType,villaHouseArea,
        time,currentUser,number,path,rootPath,null,null,securityMoney,description,monthlyRent,maintenanceCharge
        ,null,null,null,null,null,null,null)

        db.document(path).set(data)
        db.document(rootPath).set(data)

        val date =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabel(path, rootPath)
                }

        chipAvailability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etSelectAvailability.visibility= View.GONE
                availability=chipAvailability.text.toString()
                db.document(path)
                        .update("availability", availability)
                db.document(rootPath)
                        .update("availability", availability)
            }else{
                etSelectAvailability.visibility= View.VISIBLE
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

        tags(chipAmenities,path,rootPath,"amenities",amenitiesList!!)
        tagss(chipBathroom,path,rootPath,"bathroomCount",bathroomCount!!)
        tagss(chipHall,path,rootPath,"hallCount",hallCount!!)
        tagss(chipBedroom,path,rootPath,"bathroomCount",bedRoomCount!!)
        tagss(chipTenants,path,rootPath,"tenants",tenants!!)
        tagss(chipPujaRoom,path,rootPath,"pujaRoomCount",pujaRoomCount!!)
        tagss(chipFurnishing,path,rootPath,"furnishingStatus",furnishingStatus!!)

        btnUpload.setOnClickListener {
            if (bathroomCount!!.size>0 && bedRoomCount!!.size>0 && hallCount!!.size>0 &&
                    availability!="" && pujaRoomCount!!.size>0 && tenants!!.size>0 &&
                    furnishingStatus!!.size>0 ) {
                val intent = Intent(this, OwnerActivity4::class.java)
                intent.putExtra("path", path)
                intent.putExtra("rootPath", rootPath)
                intent.putExtra("propertyType", propertyType)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select required fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

     private fun tags(chipGroup: ChipGroup, path: String, rootPath: String,field:String,propertySubType2:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
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

    private fun tagss(chipGroup: ChipGroup, path: String, rootPath: String, field:String,propertySubType2:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener {_, isChecked ->
                    if (isChecked) {
                        propertySubType2.add(it.text.toString())
                        db.document(path)
                                .update(field, it.text.toString())
                        db.document(rootPath)
                                .update(field, it.text.toString())
                    } else {
                        db.document(rootPath)
                                .update(field, it.text.toString())
                        db.document(path)
                                .update(field, it.text.toString())
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

    data class UploadVillaInfo(
        val ownerName: String?,
        val email: String?,
        val city: String?,
        val fullAddress: String?,
        val latitude: String?,
        val longitude: String?,
        val propertyType: String?,
        val villaArea: String?,
        val time: String?,
        val currentUser: String?,
        val number: String?,
        val path: String,
        val rootPath: String,
        val images: ArrayList<String>?,
        val tags: ArrayList<String>?,
        val SecurityMoney: String?,
        val description: String?,
        val monthlyRent: String?,
        val maintenanceCharge: String?,
        val availability: String?,
        val amenities:ArrayList<String>?,
        val furnishingStatus:String?,
        val bathroomCount:String?,
        val pujaRoomCount:String?,
        val hallCount:String?,
        val bedRoomCount:String?
    )

}