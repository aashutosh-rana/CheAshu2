package com.bcebhagalpur.cheashu.dashboard.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bcebhagalpur.cheashu.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class MarriageHallActivity2 : AppCompatActivity() {

    private lateinit var txtSelectModel: TextView
    private lateinit var linearPerPlate: LinearLayout
    private lateinit var linearPerDay: LinearLayout
    private lateinit var etVeg: TextInputLayout
    private lateinit var etNonVeg: TextInputLayout
    private lateinit var etPricePerDay: TextInputLayout
    private lateinit var etFoodCharge: TextInputLayout
    private lateinit var etAdvanceDeposit: TextInputLayout
    private lateinit var etSecurityDeposit: TextInputLayout
    private lateinit var etDecorationCharge: TextInputLayout
    private lateinit var etElectricityCharge: TextInputLayout
    private lateinit var etCancellationCharge: TextInputLayout
    private lateinit var etTotalCharge: TextInputLayout
    private lateinit var etOtherCharge: TextInputLayout

    //    private lateinit var chipAmenities:ChipGroup
    private lateinit var btnUpload: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marriage_hall2)

        txtSelectModel = findViewById(R.id.txtSelectModel)
        linearPerPlate = findViewById(R.id.linearPerPlate)
        linearPerDay = findViewById(R.id.linearPerDay)
        etVeg = findViewById(R.id.etVeg)
        etNonVeg = findViewById(R.id.etNonVeg)
        etPricePerDay = findViewById(R.id.etRatePerDay)
        etFoodCharge = findViewById(R.id.etFoodCharge)
        etAdvanceDeposit = findViewById(R.id.etAdvanceCharge)
        etSecurityDeposit = findViewById(R.id.etSecurityCharge)
        etDecorationCharge = findViewById(R.id.etDecorationCharge)
        etElectricityCharge = findViewById(R.id.etElectricityCharge)
        etCancellationCharge = findViewById(R.id.etCancellationCharge)
        etTotalCharge = findViewById(R.id.etTotalCharge)
        etOtherCharge = findViewById(R.id.etOtherCharge)
//        chipAmenities=findViewById(R.id.chipAmenities)
        btnUpload = findViewById(R.id.btnUpload)

        txtSelectModel.setOnClickListener {
            val subject = arrayOf(
                "Per plate model", "Per day rental model","both"
            )

            val mAlertDialogBuilder =
                android.app.AlertDialog.Builder(this@MarriageHallActivity2)
            mAlertDialogBuilder.setTitle("Select class")
            mAlertDialogBuilder.setItems(subject) { _, which ->
                when (which) {
                    which -> {
                        txtSelectModel.text = subject[which]
                        when (txtSelectModel.text) {
                            subject[0] -> {
                                linearPerPlate.visibility = View.VISIBLE
                                linearPerDay.visibility = View.GONE
                            }
                            subject[1] -> {
                                linearPerDay.visibility = View.VISIBLE
                                linearPerPlate.visibility = View.GONE
                            }
                            else -> {
                                linearPerDay.visibility = View.GONE
                                linearPerPlate.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            val mAlertDialog = mAlertDialogBuilder.create()
            mAlertDialog.show()
        }
        updateUi()
    }

    private fun updateUi() {

        btnUpload.setOnClickListener {
            if (txtSelectModel.text != "Select price model") {
                if (txtSelectModel.text == "Per plate model") {
                    if (!TextUtils.isEmpty(etTotalCharge.editText!!.text) && !TextUtils.isEmpty(
                            etAdvanceDeposit.editText!!.text
                        ) &&
                        !TextUtils.isEmpty(etVeg.editText!!.text) && !TextUtils.isEmpty(etNonVeg.editText!!.text)
                    ) {

                        uploadToDatabase()

                    } else {
                        Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (txtSelectModel.text == "Per day rental model") {
                        if (!TextUtils.isEmpty(etTotalCharge.editText!!.text) && !TextUtils.isEmpty(
                                etAdvanceDeposit.editText!!.text
                            ) &&
                            !TextUtils.isEmpty(etPricePerDay.editText!!.text) && !TextUtils.isEmpty(
                                etFoodCharge.editText!!.text
                            )
                        ) {
                            uploadToDatabase()
                        } else {
                            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            } else {
                Toast.makeText(this, "Select price model", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun uploadToDatabase() {

        val hallName = intent.getStringExtra("hallName")
        val ownerName = intent.getStringExtra("ownerName")
        val email = intent.getStringExtra("email")
        val city = intent.getStringExtra("city")
        val fullAddress = intent.getStringExtra("fullAddress")
        val hallArea = intent.getStringExtra("hallArea")
        val capacity = intent.getStringExtra("capacity")
        val longitude = intent.getStringExtra("longitude")
        val latitude = intent.getStringExtra("latitude")
        val propertyType = intent.getStringExtra("propertyType")
        val suitableFor = intent.getStringExtra("suitableFor")
        val suitableForArray = intent.getStringArrayListExtra("suitableForArray")
        val db1=intent.getStringExtra("db1")
        val time = Calendar.getInstance().time.toString()
        val number = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val db = FirebaseFirestore.getInstance()
        val path = "MarriageHall/$currentUser$db1"
        val rootPath = "All/$currentUser$db1"

        val data = UploadMarriageInfo(
            hallName,
            ownerName,
            email,
            city,
            fullAddress,
            latitude,
            longitude,
            propertyType,
            hallArea,
            capacity!!.toInt(),
            suitableForArray,
            time,
            currentUser,
            number,
            path,
            rootPath,
            null,
            null,
            txtSelectModel.text.toString(),
            etVeg.editText!!.text.toString(),
            etNonVeg.editText!!.text.toString(),
            etPricePerDay.editText!!.text.toString(),
            etFoodCharge.editText!!.text.toString(),
            etAdvanceDeposit.editText!!.text.toString(),
            etSecurityDeposit.editText!!.text.toString(),
            etDecorationCharge.editText!!.text.toString(),
            etElectricityCharge.editText!!.text.toString(),
            etCancellationCharge.editText!!.text.toString(),
            etTotalCharge.editText!!.text.toString(),
            etOtherCharge.editText!!.text.toString(),
            null,
            null,
            null,
        db1)
        db.document(path).set(data)
        db.document(rootPath).set(data)
        val intent = Intent(this, OwnerActivity4::class.java)
        intent.putExtra("path", path)
        intent.putExtra("rootPath", rootPath)
        intent.putExtra("propertyType", propertyType)
        startActivity(intent)
    }

    data class UploadMarriageInfo(
            val hallName: String?,
            val ownerName: String?,
            val email: String?,
            val city: String?,
            val fullAddress: String?,
            val latitude: String?,
            val longitude: String?,
            val propertyType: String?,
            val hallArea: String?,
            val capacity: Int?,
            val suitableFor: ArrayList<String>?,
            val time: String?,
            val currentUser: String?,
            val number: String?,
            val path: String,
            val rootPath: String,
            val images: ArrayList<String>?,
            val tags: ArrayList<String>?,
            val priceModel: String,
            val Veg: String?,
            val NonVeg: String?,
            val PricePerDay: String?,
            val FoodCharge: String?,
            val AdvanceDeposit: String?,
            val SecurityDeposit: String?,
            val DecorationCharge: String?,
            val ElectricityCharge: String?,
            val CancellationCharge: String?,
            val TotalCharge: String?,
            val OtherCharge: String?,
            val acRoom: String?,
            val nonAcRoom: String?,
            val description: String?,
            val documentId:String?

    )

}