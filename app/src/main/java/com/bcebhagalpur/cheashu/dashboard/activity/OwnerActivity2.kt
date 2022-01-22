package com.bcebhagalpur.cheashu.dashboard.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.bcebhagalpur.cheashu.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class OwnerActivity2 : AppCompatActivity() {

    private lateinit var chipPropertySubType: ChipGroup
    private var propertySubTypeString=""
    private lateinit var chipTenants: ChipGroup
    var tenantsString=""
    private lateinit var chipBathroom: ChipGroup
    var bathRoomString=""
    private lateinit var chipFurnishing: ChipGroup
    var furnishingString=""
    private lateinit var chipAmenities: ChipGroup
    private var amenitiesList:ArrayList<String>?= arrayListOf()
    private lateinit var chipAvailability: Chip
    private var availability:String?=""
    private lateinit var btnUpload: Button
    private lateinit var etFloorStatus:EditText
    var floorString=""
    private lateinit var etSelectAvailability:EditText
    private val myCalendar = Calendar.getInstance()
    private var tagList:ArrayList<String>?= arrayListOf()


    private val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner2)

        chipPropertySubType = findViewById(R.id.chipPropertySubtype)
        chipTenants = findViewById(R.id.chipTenants)
        chipBathroom = findViewById(R.id.chipBathroom)
        chipFurnishing = findViewById(R.id.chipFurnishing)
        chipAmenities = findViewById(R.id.chipAmenities)
        btnUpload = findViewById(R.id.btnUpload)
        chipAvailability=findViewById(R.id.chipAvailability)
        etFloorStatus=findViewById(R.id.etFloorStatus)
        etSelectAvailability=findViewById(R.id.etSelectAvailability)

        val ownerName=intent.getStringExtra("name")
        val email=intent.getStringExtra("email")
        val address=intent.getStringExtra("address")
        val city=intent.getStringExtra("city")
        val latitude=intent.getStringExtra("latitude")
        val longitude=intent.getStringExtra("longitude")
        val propertyType=intent.getStringExtra("propertyType")
        val monthlyRent=intent.getStringExtra("monthlyRent")
        val maintenanceCharge=intent.getStringExtra("maintenanceCharge")
        val securityMoney=intent.getStringExtra("securityMoney")
        val buildingName=intent.getStringExtra("buildingName")
        val time=Calendar.getInstance().time.toString()
        val number = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        val db1=intent.getStringExtra("db1")
        val path="$propertyType/$currentUser$db1"
        val rootPath="All/$currentUser$db1"

        tagPropertySubType(chipPropertySubType)
        tagTenants(chipTenants)
        tagBathroom(chipBathroom)
        tagFurnishing(chipFurnishing)
        amenitiesFlat(chipAmenities)

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateLabel(path, rootPath)
        }

        chipAvailability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etSelectAvailability.visibility= View.GONE
                availability=chipAvailability.text.toString()
                Toast.makeText(this,availability,Toast.LENGTH_SHORT).show()
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
            availability=etSelectAvailability.text.toString()

        }

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
                        floorString=subject[which]
                    }
                }
            }
            val mAlertDialog = mAlertDialogBuilder.create()
            mAlertDialog.show()
        }

        btnUpload.setOnClickListener {
            if (propertySubTypeString!="" && tenantsString!="" && furnishingString!="" &&
                    bathRoomString!="" && availability!="" && tagList!!.size>0 && floorString!="")
                    {
                val data= FlatDataInfo(ownerName,email,address,city,latitude,longitude, propertyType,null,tagList,time,currentUser,
                        "$currentUser$db1",
                        number,path,rootPath,maintenanceCharge,monthlyRent!!.toDouble(),securityMoney,tenantsString,bathRoomString,furnishingString,amenitiesList!!
                        ,etFloorStatus.text.toString(),buildingName,availability,propertySubTypeString)
                        db.document(rootPath).set(data)
                        db.document(path).set(data)
                val intent=Intent(this,OwnerActivity4::class.java)
                intent.putExtra("rootPath",rootPath)
                intent.putExtra("path",path)
                startActivity(intent)
            }else{
                Toast.makeText(this,propertySubTypeString+tenantsString+furnishingString+bathRoomString+availability+floorString,Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun amenitiesFlat(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                      amenitiesList!!.add(it.text.toString() )
                        tagList!!.add(it.text.toString())
                    } else {
                       amenitiesList!!.remove(it.text.toString())
                        tagList!!.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

//    private fun filterTags(chipGroup: ChipGroup) {
//        chipGroup.children.forEach {
//            try {
//                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
//                    if (isChecked) {
//                        tagList!!.add(it.text.toString())
//                    } else {
//                      tagList!!.remove(it.text.toString())
//                    }
//                }
//            } catch (e: TypeCastException) {
//                e.printStackTrace()
//            }
//        }
//
//    }

    private fun tagPropertySubType(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                       propertySubTypeString=it.text.toString()
                        Toast.makeText(this,propertySubTypeString,Toast.LENGTH_SHORT).show()
                        tagList!!.add(propertySubTypeString)
                    }else{
                        tagList!!.remove(propertySubTypeString)
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun tagTenants(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tenantsString=it.text.toString()
                        Toast.makeText(this,tenantsString,Toast.LENGTH_SHORT).show()
                        tagList!!.add(tenantsString)
                    }else{
                        tagList!!.remove(tenantsString)
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun tagFurnishing(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener {_, isChecked ->
                    if (isChecked) {
                        furnishingString=it.text.toString()
                        Toast.makeText(this,furnishingString,Toast.LENGTH_SHORT).show()
                        tagList!!.add(furnishingString)
                    }else{
                        tagList!!.remove(furnishingString)
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun tagBathroom(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        bathRoomString=it.text.toString()
                        Toast.makeText(this,bathRoomString,Toast.LENGTH_SHORT).show()
                        tagList!!.add(bathRoomString)
                    }else{
                        tagList!!.remove(bathRoomString)
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
    data class FlatDataInfo (
            val ownerName    :String?=null,
            val email       :String?=null,
            val address     :String?=null,
            val city        :String?=null,
            val latitude    :String?=null,
            val longitude   :String?=null,
            val propertyType:String?=null,
            val images      :ArrayList<String>?=null,
            val tags        :ArrayList<String>?=null,
            val time        :String?=null,
            val id          :String?=null,
            val documentId:String?=null,
            val number      : String?=null,
            val path        : String?=null,
            val rootPath    : String?=null,
            val maintenanceCharge:String?=null,
            val monthlyRent : Double?=null,
            val securityMoney: String?=null,
            val tenants: String?=null,
            val bathroomCount:String?=null,
            val furnishingStatus: String?=null,
            val amenities:ArrayList<String>?=null,
            val floorStatus: String?=null,
            val buildingName:String?=null,
            val availability:String?=null,
            val propertySubType:String?=null,
    )
}
