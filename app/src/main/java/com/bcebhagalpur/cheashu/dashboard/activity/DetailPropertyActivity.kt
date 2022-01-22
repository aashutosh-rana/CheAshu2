package com.bcebhagalpur.cheashu.dashboard.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bcebhagalpur.cheashu.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.ArrayList

class DetailPropertyActivity : AppCompatActivity() {

    private lateinit var imgThumbnail:ImageView
    private lateinit var txtImageCount:TextView
    private lateinit var txtMonthlyRent:TextView
    private lateinit var txtPropertySubType:TextView
    private lateinit var txtPostedDate:TextView
    private lateinit var txtCity:TextView
    private lateinit var txtFullAddress:TextView
    private lateinit var txtBuildingName:TextView
    private lateinit var txtOwnerName:TextView
    private lateinit var btnCallNow:Button
    private lateinit var btnChatOwner:Button
    private lateinit var btnShowLocation:Button
    private lateinit var txtAvailability:TextView
    private lateinit var txtTenants:TextView
    private lateinit var txtBathroomCount:TextView
    private lateinit var txtFloor:TextView
    private lateinit var txtFurnishing:TextView
    private lateinit var txtMaintenanceCharge:TextView
    private lateinit var txtSecurityMoney:TextView
    private lateinit var txtAmenities:TextView

    private val db=FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_property)

        imgThumbnail=findViewById(R.id.imgThumbnailDetail)
        txtImageCount=findViewById(R.id.txtImageCount)
        txtMonthlyRent=findViewById(R.id.txtMonthlyRent)
        txtPropertySubType=findViewById(R.id.txtPropertySubType)
        txtCity=findViewById(R.id.txtCity)
        txtFullAddress=findViewById(R.id.txtFullAddress)
        txtBuildingName=findViewById(R.id.txtBuildingName)
        txtOwnerName=findViewById(R.id.txtOwnerName)
        btnCallNow=findViewById(R.id.btnCallNow)
        btnChatOwner=findViewById(R.id.btnChatOwner)
        btnShowLocation=findViewById(R.id.btnShowLocation)
        txtAvailability=findViewById(R.id.txtAvailability)
        txtTenants=findViewById(R.id.txtTenants)
        txtBathroomCount=findViewById(R.id.txtBathroomCount)
        txtFloor=findViewById(R.id.txtFloor)
        txtFurnishing=findViewById(R.id.txtfurnishing)
        txtPostedDate=findViewById(R.id.txtPostedDate)
        txtMaintenanceCharge=findViewById(R.id.txtMaintenanceCharge)
        txtSecurityMoney=findViewById(R.id.txtSecurityMoney)
        txtAmenities=findViewById(R.id.txtAmenities)

        fetchData()

    }

    private fun fetchData(){
        val path=intent.getStringExtra("path")
        intent.getStringExtra("name")
        val monthlyRent=intent.getStringExtra("monthlyRent")
        val city=intent.getStringExtra("city")
        val address=intent.getStringExtra("address")
        val propertyType=intent.getStringExtra("propertyType")
        val latitude=intent.getStringExtra("latitude")
        val longitude=intent.getStringExtra("longitude")
        txtMonthlyRent.text = "₹$monthlyRent/month"
        txtCity.text=city
        txtFullAddress.text=address

        val flatDataInfo=FlatDataInfo()
        db.document(path!!).get().addOnSuccessListener {
            if (it.exists()){
                val bathroomCount=it.get("bathroomCount")
                txtBathroomCount.text=bathroomCount.toString()
                val furnishing=it.get("furnishingStatus")
               txtFurnishing.text=furnishing.toString()
                val propertySubType=it.get("propertySubType")
                txtPropertySubType.text=propertySubType.toString()
                val date=it.get("time")
                txtPostedDate.text=date.toString()
                val availability=it.get("availability")
                txtAvailability.text=availability.toString()
                val tenants=it.get("tenants")
                txtTenants.text=tenants.toString()
                val maintenanceCharge=it.get("maintenanceCharge")
                txtMaintenanceCharge.text=maintenanceCharge.toString()
                val securityMoney=it.get("securityMoney")
                txtSecurityMoney.text=securityMoney.toString()
                val amenities=it.get("amenities")
                txtAmenities.text=amenities.toString()
                val floor=it.get("floorStatus")
                txtFloor.text=floor.toString()
                val images=it.get("images")
                

            }
        }
    }
    class FlatDataInfo (
       val ownerName: String? = null,
       val email: String? = null,
       val address: String? = null,
       val city: String? = null,
       val latitude: String? = null,
       val longitude: String? = null,
       val propertyType: String? = null,
       val images: ArrayList<String>? = null,
       val tags: ArrayList<String>? = null,
       val time: String? = null,
       val id: String? = null,
       val documentId: String? = null,
       val number: String? = null,
       val path: String? = null,
       val rootPath: String? = null,
       val maintenanceCharge: String? = null,
       val monthlyRent: String? = null,
       val securityMoney: String? = null,
       val tenants: String? = null,
       val bathroomCount: String? = null,
       val furnishingStatus: String? = null,
       val amenities: String? = null,
       val floorStatus: String? = null,
       val description: String? = null,
       val availability: String? = null,
       val propertySubType: String? = null
    )

}