package com.bcebhagalpur.cheashu.dashboard.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MarriageHallDetailActivity : AppCompatActivity() {

    private lateinit var imgThumbnailMarriageHall:ImageView
    private lateinit var txtImageCount:TextView
    private lateinit var txtHallName:TextView
    private lateinit var txtCity:TextView
    private lateinit var txtFullAddress:TextView
    private lateinit var txtHallArea:TextView
    private lateinit var txtCapacity:TextView
    private lateinit var txtSuitableFor:TextView
    private lateinit var txtPriceModel:TextView
    private lateinit var txtVeg:TextView
    private lateinit var txtNonVeg:TextView
    private lateinit var txtPerDayRent:TextView
    private lateinit var txtAdvanceDeposit:TextView
    private lateinit var txtSecurityDeposit:TextView
    private lateinit var txtDecorationCharge:TextView
    private lateinit var txtElectricityCharge:TextView
    private lateinit var txtCancellationCharge:TextView
    private lateinit var txtOtherCharge:TextView
    private lateinit var txtTotalCharge:TextView
    private lateinit var txtAcRoom:TextView
    private lateinit var txtNonAcRoom:TextView
    private lateinit var txtDescription:TextView
    private lateinit var recyclerSimilarMarriageHall:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marriage_hall_detail)

        imgThumbnailMarriageHall=findViewById(R.id.imgThumbnailDetailMarriageHall)
        txtImageCount=findViewById(R.id.txtImageCount)
        txtHallName=findViewById(R.id.txtHallName)
        txtCity=findViewById(R.id.txtCity)
        txtFullAddress=findViewById(R.id.txtFullAddress)
        txtHallArea=findViewById(R.id.txtHallArea)
        txtCapacity=findViewById(R.id.txtCapacity)
        txtSuitableFor=findViewById(R.id.txtSuitableFor)
        txtPriceModel=findViewById(R.id.txtPriceModel)
        txtVeg=findViewById(R.id.txtVeg)
        txtNonVeg=findViewById(R.id.txtNonVeg)
        txtPerDayRent=findViewById(R.id.txtRentPerDay)
        txtAdvanceDeposit=findViewById(R.id.txtAdvanceDeposit)
        txtSecurityDeposit=findViewById(R.id.txtSecurityDeposit)
        txtDecorationCharge=findViewById(R.id.txtDecorationCharge)
        txtElectricityCharge=findViewById(R.id.txtElectricityCharge)
        txtCancellationCharge=findViewById(R.id.txtCancellationCharge)
        txtOtherCharge=findViewById(R.id.txtOtherCharge)
        txtTotalCharge=findViewById(R.id.txtTotalCharge)
        txtAcRoom=findViewById(R.id.txtAcRoom)
        txtNonAcRoom=findViewById(R.id.txtNonAcRoom)
        txtDescription=findViewById(R.id.txtDescription)
        recyclerSimilarMarriageHall=findViewById(R.id.recyclerSimilarMarriageHall)

        val hallName=intent.getStringExtra("name")
        val images=intent.getStringArrayExtra("images")
        val city=intent.getStringExtra("city")
        val path=intent.getStringExtra("path")
        val fullAddress=intent.getStringExtra("address")
        val propertyType=intent.getStringExtra("propertyType")
        val latitude=intent.getStringExtra("latitude")
        val longitude=intent.getStringExtra("longitude")
        val mobileNumber=intent.getStringExtra("number")

        txtImageCount.text=images!!.size.toString()
        if (images.isNotEmpty()){
            Picasso.get().load(images[0]).fit().error(R.drawable.welcome_walkthrough).into(imgThumbnailMarriageHall)
        }
        txtHallName.text=hallName
        txtCity.text=city
        txtFullAddress.text=fullAddress

        val db=FirebaseFirestore.getInstance()

        db.document(path!!).get().addOnSuccessListener {
            if (it.exists())
            {

//                val bathroomCount=it.get("bathroomCount")
//                txtBathroomCount.text=bathroomCount.toString()
//                val furnishing=it.get("furnishingStatus")
//                txtFurnishing.text=furnishing.toString()
//                val propertySubType=it.get("propertySubType")
//                txtPropertySubType.text=propertySubType.toString()
//                val date=it.get("time")
//                txtPostedDate.text=date.toString()
//                val availability=it.get("availability")
//                txtAvailability.text=availability.toString()
//                val tenants=it.get("tenants")
//                txtTenants.text=tenants.toString()
//                val maintenanceCharge=it.get("maintenanceCharge")
//                txtMaintenanceCharge.text=maintenanceCharge.toString()
//                val securityMoney=it.get("securityMoney")
//                txtSecurityMoney.text=securityMoney.toString()
//                val amenities=it.get("amenities")
//                txtAmenities.text=amenities.toString()
//                val floor=it.get("floorStatus")
//                txtFloor.text=floor.toString()
//                val images=it.get("images")
            }
        }
    }
}