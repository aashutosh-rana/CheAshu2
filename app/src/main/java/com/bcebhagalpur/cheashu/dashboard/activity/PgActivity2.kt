package com.bcebhagalpur.cheashu.dashboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.core.view.children
import com.bcebhagalpur.cheashu.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PgActivity2 : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var chipTenants:ChipGroup
    private lateinit var chipFacilities:ChipGroup
    private lateinit var chipAmenities:ChipGroup
    private lateinit var chipRule:ChipGroup
    private lateinit var btnUploadProperty:Button
    private lateinit var etDescription:TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pg2)

        chipTenants=findViewById(R.id.chipTenants)
        chipFacilities=findViewById(R.id.chipRoomFacilities)
        chipAmenities=findViewById(R.id.chipAmenities)
        chipRule=findViewById(R.id.chipRule)
        etDescription=findViewById(R.id.etDescription)
        btnUploadProperty=findViewById(R.id.btnUpload)

        val ownerName=intent.getStringExtra("ownerName")
        val pgName=intent.getStringExtra("PgName")
        val email=intent.getStringExtra("email")
        val cityName=intent.getStringExtra("cityName")
        val fullAddress=intent.getStringExtra("fullAddress")
        val singleRoomCount=intent.getStringExtra("singleRoomCount")
        val doubleRoomCount=intent.getStringExtra("doubleRoomCount")
        val tripleRoomCount=intent.getStringExtra("tripleRoomCount")
        val singleRoomRent=intent.getStringExtra("singleRoomRent")
        val doubleRoomRent=intent.getStringExtra("doubleRoomRent")
        val tripleRoomRent=intent.getStringExtra("tripleRoomRent")
        val advanceDeposit=intent.getStringExtra("advanceDeposit")
        val latitude=intent.getStringExtra("latitude")
        val longitude=intent.getStringExtra("longitude")
        val propertyType=intent.getStringExtra("propertyType")
        val db1=intent.getStringExtra("db1")

        val time = Calendar.getInstance().time.toString()
        val number = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val path = "$propertyType/$currentUser/data/$db1"
        val rootPath = "All/$db1"

        val data = UploadPgInfo(ownerName,pgName,email,cityName,fullAddress,latitude,longitude,propertyType,time,
        currentUser,number,path,rootPath,null,null,null,singleRoomCount,doubleRoomCount,
        tripleRoomCount,singleRoomRent,doubleRoomRent,tripleRoomRent,advanceDeposit,null,null,
        null,null,null,null,null,null)
        db.document(path).set(data)
        db.document(rootPath).set(data)

        tags(chipAmenities,path,rootPath,"amenities")
        tags(chipFacilities,path,rootPath,"facilities")
        tags(chipRule,path,rootPath,"rule")
        tags(chipTenants,path,rootPath,"tenants")

        btnUploadProperty.setOnClickListener {
                db.document(path)
                        .update("description", etDescription.editText!!.text.toString())
                db.document(rootPath)
                        .update("description", etDescription.editText!!.text.toString())
                val intent = Intent(this, OwnerActivity4::class.java)
                intent.putExtra("path", path)
                intent.putExtra("rootPath", rootPath)
                intent.putExtra("propertyType", propertyType)
                startActivity(intent)
        }

    }

    private fun tags(chipGroup: ChipGroup, path: String, rootPath: String, field:String) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
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
    private fun filterTags(chipGroup: ChipGroup, path: String, rootPath: String) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        db.document(path)
                                .update("tags", FieldValue.arrayUnion(it.text.toString()))
                        db.document(rootPath)
                                .update("tags", FieldValue.arrayUnion(it.text.toString()))
                    } else {
                        db.document(rootPath)
                                .update("tags", FieldValue.arrayRemove(it.text.toString()))
                        db.document(path)
                                .update("tags", FieldValue.arrayRemove(it.text.toString()))
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    data class UploadPgInfo(
            val ownerName: String?,
            val pgName: String?,
            val email: String?,
            val city: String?,
            val fullAddress: String?,
            val latitude: String?,
            val longitude: String?,
            val propertyType: String?,
            val time: String?,
            val currentUser: String?,
            val number: String?,
            val path: String,
            val rootPath: String,
            val images: ArrayList<String>?,
            val tags: ArrayList<String>?,
            val description: String?,
            val singleRoomCount:String?,
            val doubleRoomCount:String?,
            val tripleRoomCount:String?,
            val singleRoomRent:String?,
            val doubleRoomRent:String?,
            val tripleRoomRent:String?,
            val advanceDeposit:String?,
            val tenants:String?,
            val facilities:String?,
            val amenities:String?,
            val rule:String?,
            val messCharge:String?,
            val messChargeType:String?,
            val messTime:String?,
            val foodType:String?
    )
}