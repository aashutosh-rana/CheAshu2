package com.bcebhagalpur.cheashu.dashboard.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.renter.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.ArrayList

class FilterFragment : Fragment() {

    //location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude:Double = 0.0
    var longitude:Double=0.0

    private lateinit var btnFilter:Button
    //budget
    private lateinit var etMinRent:TextInputLayout
    private lateinit var etMaxRent:TextInputLayout
    private lateinit var linearBudget:LinearLayout

    //city
    private lateinit var city:TextInputLayout
    private lateinit var currentCity:EditText
    //linear
    private lateinit var linearPg:LinearLayout
    private lateinit var linearSimpleFlat:LinearLayout
    private lateinit var linearVilla:LinearLayout
    private lateinit var linearMarriageHall:LinearLayout
    private lateinit var linearCommercialSpace:LinearLayout

    //textPropertyType
    private lateinit var simpleFlat:TextView
    private lateinit var pg:TextView
    private lateinit var apartment:TextView
    private lateinit var marriageHall:TextView
    private lateinit var houses:TextView
    private lateinit var commercialSpace:TextView
    private lateinit var hostel:TextView
    private lateinit var villa:TextView
    private lateinit var doubleRoom:TextView
    private lateinit var singleRoom:TextView
    private lateinit var txtBudget:TextView

    //linearSimpleFlat
    //1 bedrooms
    private lateinit var oneBhk:TextView
    private lateinit var twoBhk:TextView
    private lateinit var threeBhk:TextView
    private var bedRoomType=""
    //2 tenants
    private lateinit var family:TextView
    private lateinit var bachelor:TextView
    private lateinit var both:TextView
    private var tenants=""
    //3 bathrooms
    private lateinit var one:TextView
    private lateinit var two:TextView
    private lateinit var three:TextView
    private lateinit var four:TextView
    private lateinit var five:TextView
    private lateinit var fivePlus:TextView
    private var bathroom=""
    //4 furnishing
    private lateinit var furnished:TextView
    private lateinit var semiFurnished:TextView
    private lateinit var unFurnished:TextView
    private var furnishing=""
    //5 amenities
    private lateinit var chipAmenitiesSimpleFlat:ChipGroup
    private var amenitiesListSimpleFlat:ArrayList<String>?= arrayListOf()

    //linearPg
    //1 sharing bed type
    private lateinit var singleBed:TextView
    private lateinit var doubleBed:TextView
    private lateinit var tripleBed:TextView
    private var bedRoomTypePg=""
    //2 tenants
    private lateinit var boys:TextView
    private lateinit var girls:TextView
    private lateinit var bothPg:TextView
    private var tenantsPg=""
    //3 food
    private lateinit var mess:TextView
    private lateinit var selfCooking:TextView
    private var foodPg=""
    //4 facilities
    private lateinit var chipRoomFacilitiesPg:ChipGroup
    private var roomFacilitiesListPg:ArrayList<String>?= arrayListOf()
    //5 amenities
    private lateinit var chipAmenitiesPg:ChipGroup
    private var amenitiesListPg:ArrayList<String>?= arrayListOf()

    //linearMarriageHall
    //1 price model
//    private lateinit var perPlate:TextView
//    private lateinit var perDay:TextView
//    private var priceModel=""
    //2 per plate model price
//    private lateinit var linearPerPlateModel:LinearLayout
//    private lateinit var vegMinBudget:TextInputLayout
//    private lateinit var vegMaxBudget:TextInputLayout
//    private lateinit var nonVegMinBudget:TextInputLayout
//    private lateinit var nonVegMaxBudget:TextInputLayout
//    //3 per day rental model
//    private lateinit var linearPerDayModel:LinearLayout
//    private lateinit var perDayMaxBudget:TextInputLayout
//    private lateinit var perDayMinBudget:TextInputLayout
    //4 capacity
    private lateinit var maxCapacity:TextInputLayout
    //5 room with
    private lateinit var ac:TextView
    private lateinit var nonAc:TextView
    private var marriageHallRomType=""
    //6 event type
    private lateinit var chipEventType:ChipGroup
    private var eventTypeList:ArrayList<String>?= arrayListOf()
    //6 other facilities
    private lateinit var chipAmenitiesMarriageHall:ChipGroup
    private var amenitiesListMarriageHall:ArrayList<String>?= arrayListOf()

    //linear commercial space
    //1 property uses
    private lateinit var shop:TextView
    private lateinit var showRoom:TextView
    private lateinit var wareHouse:TextView
    private lateinit var coachingCenter:TextView
    private lateinit var office:TextView
    private var propertyTypeCommercial=""
    //2 distance from road
    private lateinit var maxDistance:TextInputLayout
    private lateinit var minDistance:TextInputLayout
    //3 furnishing
    private lateinit var furnishedCommercial:TextView
    private lateinit var semiFurnishedCommercial:TextView
    private lateinit var unFurnishedCommercial:TextView
    private var furnishingCommercial=""
    //4 facilities
    private lateinit var chipAmenitiesCommercial:ChipGroup
    private var amenitiesListCommercial:ArrayList<String>?= arrayListOf()

    //linear villa
    private lateinit var chipBedRoom:ChipGroup
    private var bedRoomVilla=""
    private lateinit var chipHall:ChipGroup
    private var hallVilla=""
    private lateinit var chipPujaRoomcount:ChipGroup
    private var pujaroomVilla=""
    private lateinit var chipTenants:ChipGroup
    private var tenantsVilla=""
    private lateinit var chipBathroom:ChipGroup
    private var bathRoomVilla=""
    // furnishing
    private lateinit var furnishedVilla:TextView
    private lateinit var semiFurnishedVilla:TextView
    private lateinit var unFurnishedVilla:TextView
    private var furnishingVilla=""
    private lateinit var chipAmenitiesVilla:ChipGroup
    private var amenitiesListVilla:ArrayList<String>?= arrayListOf()


    var propertyType:String=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view= inflater.inflate(R.layout.fragment_filter, container, false)

        displayLocationSettingsRequest(activity as Context)

        btnFilter=view.findViewById(R.id.btnFilter)
        linearPg=view.findViewById(R.id.linearPg)
        linearSimpleFlat=view.findViewById(R.id.linearSimpleFlat)
        linearCommercialSpace=view.findViewById(R.id.linearCommercialSpace)
        linearMarriageHall=view.findViewById(R.id.linearMarriageHall)
        linearVilla=view.findViewById(R.id.linearVilla)
        simpleFlat=view.findViewById(R.id.simpleFlat)
        pg=view.findViewById(R.id.pg)
        apartment=view.findViewById(R.id.apartment)
        marriageHall=view.findViewById(R.id.marriageHall)
        houses=view.findViewById(R.id.houses)
        commercialSpace=view.findViewById(R.id.commercialSpace)
        hostel=view.findViewById(R.id.hostel)
        villa=view.findViewById(R.id.villa)
        doubleRoom=view.findViewById(R.id.doubleRoom)
        singleRoom=view.findViewById(R.id.singleRoom)
        txtBudget=view.findViewById(R.id.txtBudget)

        //linearSimpleFlat
        oneBhk=view.findViewById(R.id.oneBhk)
        twoBhk=view.findViewById(R.id.twoBhk)
        threeBhk=view.findViewById(R.id.threeBhk)
        family=view.findViewById(R.id.family)
        bachelor=view.findViewById(R.id.bachelor)
        both=view.findViewById(R.id.both)
        one=view.findViewById(R.id.one)
        two=view.findViewById(R.id.two)
        three=view.findViewById(R.id.three)
        four=view.findViewById(R.id.four)
        five=view.findViewById(R.id.five)
        fivePlus=view.findViewById(R.id.fivePlus)
        furnished=view.findViewById(R.id.furnished)
        semiFurnished=view.findViewById(R.id.semiFurnished)
        unFurnished=view.findViewById(R.id.unFurnished)
        chipAmenitiesSimpleFlat=view.findViewById(R.id.chipAmenitiesSimpleFlat)

        //linearPg
         singleBed=view.findViewById(R.id.singleBed)
         doubleBed=view.findViewById(R.id.doubleBed)
         tripleBed=view.findViewById(R.id.tripleBed)
         boys=view.findViewById(R.id.boys)
         girls=view.findViewById(R.id.girls)
         bothPg=view.findViewById(R.id.bothPg)
         mess=view.findViewById(R.id.mess)
         selfCooking=view.findViewById(R.id.selfCooking)
         chipRoomFacilitiesPg=view.findViewById(R.id.chipRoomFacilitiesPg)
         chipAmenitiesPg=view.findViewById(R.id.chipAmenitiesPg)

        //linearMarriageHall
//         linearPerPlateModel=view.findViewById(R.id.linearPerPlateModel)
//         linearPerDayModel=view.findViewById(R.id.linearPerDayModel)
         maxCapacity=view.findViewById(R.id.maxCapacity)
         chipAmenitiesMarriageHall=view.findViewById(R.id.chipAmenitiesMarriageHall)
//         perPlate=view.findViewById(R.id.perPlate)
//         perDay=view.findViewById(R.id.perDay)
//         vegMinBudget=view.findViewById(R.id.vegMinBudget)
//         vegMaxBudget=view.findViewById(R.id.vegMaxBudget)
//         nonVegMinBudget=view.findViewById(R.id.nonVegMinBudget)
//         nonVegMaxBudget=view.findViewById(R.id.nonVegMaxBudget)
//         perDayMaxBudget=view.findViewById(R.id.perDayMxBudget)
//         perDayMinBudget=view.findViewById(R.id.perDayMinBudget)
         ac=view.findViewById(R.id.ac)
         nonAc=view.findViewById(R.id.nonAc)
        chipEventType=view.findViewById(R.id.chipEventType)

        //linear commercial space
         shop=view.findViewById(R.id.shop)
         showRoom=view.findViewById(R.id.showRoom)
         wareHouse=view.findViewById(R.id.wareHouse)
         coachingCenter=view.findViewById(R.id.coachingCenter)
         office=view.findViewById(R.id.office)
         maxDistance=view.findViewById(R.id.maxDistance)
         minDistance=view.findViewById(R.id.minDistance)
         furnishedCommercial=view.findViewById(R.id.furnishedCommercial)
         semiFurnishedCommercial=view.findViewById(R.id.semiFurnishedCommercial)
         unFurnishedCommercial=view.findViewById(R.id.unFurnishedCommercial)
         chipAmenitiesCommercial=view.findViewById(R.id.chipAmenitiesCommercial)

        //linear villa
         chipBedRoom=view.findViewById(R.id.chipBedRoom)
         chipHall=view.findViewById(R.id.chipHall)
         chipPujaRoomcount=view.findViewById(R.id.chipPujaRoomcount)
         chipTenants=view.findViewById(R.id.chipTenants)
         chipBathroom=view.findViewById(R.id.chipBathroom)
         furnishedVilla=view.findViewById(R.id.furnishedVilla)
         semiFurnishedVilla=view.findViewById(R.id.semiFurnishedVilla)
         unFurnishedVilla=view.findViewById(R.id.unFurnishedVilla)
         chipAmenitiesVilla=view.findViewById(R.id.chipAmenitiesVilla)

        //budget
        etMinRent=view.findViewById(R.id.etMinRent)
        etMaxRent=view.findViewById(R.id.etMaxRent)
        linearBudget=view.findViewById(R.id.linearBudget)

        //city
        city=view.findViewById(R.id.city)
        currentCity=view.findViewById(R.id.currentCity)

        currentCity.setOnClickListener {
            getCurrentLocation(city)
        }

         propertyType=simpleFlat.text.toString()
        propertyTypeUi(simpleFlat,pg,apartment,marriageHall,houses,commercialSpace,hostel,villa,doubleRoom,singleRoom
        ,linearSimpleFlat,linearCommercialSpace,linearMarriageHall,linearPg,linearVilla)
        propertyTypeUi(pg,simpleFlat,apartment,marriageHall,houses,commercialSpace,hostel,villa,doubleRoom,singleRoom
                ,linearPg,linearCommercialSpace,linearMarriageHall,linearSimpleFlat,linearVilla)
        propertyTypeUi(apartment,pg,simpleFlat,marriageHall,houses,commercialSpace,hostel,villa,doubleRoom,singleRoom
                ,linearSimpleFlat,linearCommercialSpace,linearMarriageHall,linearPg,linearVilla)
        propertyTypeUi(marriageHall,pg,apartment,simpleFlat,houses,commercialSpace,hostel,villa,doubleRoom,singleRoom
                ,linearMarriageHall,linearCommercialSpace,linearSimpleFlat,linearPg,linearVilla)
        propertyTypeUi(houses,pg,apartment,marriageHall,simpleFlat,commercialSpace,hostel,villa,doubleRoom,singleRoom
                ,linearVilla,linearCommercialSpace,linearMarriageHall,linearPg,linearSimpleFlat)
        propertyTypeUi(commercialSpace,pg,apartment,marriageHall,houses,simpleFlat,hostel,villa,doubleRoom,singleRoom
                ,linearCommercialSpace,linearSimpleFlat,linearMarriageHall,linearPg,linearVilla)
        propertyTypeUi(hostel,pg,apartment,marriageHall,houses,commercialSpace,simpleFlat,villa,doubleRoom,singleRoom
                ,linearPg,linearCommercialSpace,linearMarriageHall,linearSimpleFlat,linearVilla)
        propertyTypeUi(villa,pg,apartment,marriageHall,houses,commercialSpace,hostel,simpleFlat,doubleRoom,singleRoom
                ,linearVilla,linearCommercialSpace,linearMarriageHall,linearPg,linearSimpleFlat)
        propertyTypeUi(doubleRoom,pg,apartment,marriageHall,houses,commercialSpace,hostel,villa,simpleFlat,singleRoom
                ,linearSimpleFlat,linearCommercialSpace,linearMarriageHall,linearPg,linearVilla)
        propertyTypeUi(singleRoom,pg,apartment,marriageHall,houses,commercialSpace,hostel,villa,doubleRoom,simpleFlat
                ,linearSimpleFlat,linearCommercialSpace,linearMarriageHall,linearPg,linearVilla)


        //simple flat
        simpleFlatBedroom(oneBhk,twoBhk,threeBhk)
        simpleFlatBedroom(twoBhk,oneBhk,threeBhk)
        simpleFlatBedroom(threeBhk,twoBhk,oneBhk)
        simpleFlatTenants(family,bachelor,both)
        simpleFlatTenants(bachelor,family,both)
        simpleFlatTenants(both,family,bachelor)
        simpleFlatBathroom(one,two,three,four,five,fivePlus)
        simpleFlatBathroom(two,one,three,four,five,fivePlus)
        simpleFlatBathroom(three,two,one,four,five,fivePlus)
        simpleFlatBathroom(four,two,three,one,five,fivePlus)
        simpleFlatBathroom(five,two,three,four,one,fivePlus)
        simpleFlatBathroom(fivePlus,two,three,four,five,one)
        simpleFlatFurnishing(furnished,semiFurnished,unFurnished)
        simpleFlatFurnishing(semiFurnished,furnished,unFurnished)
        simpleFlatFurnishing(unFurnished,semiFurnished,furnished)
        simpleFlatAmenities(chipAmenitiesSimpleFlat,amenitiesListSimpleFlat!!)

        //pg
        pgBedroom(singleBed,doubleBed,tripleBed)
        pgBedroom(doubleBed,singleBed,tripleBed)
        pgBedroom(tripleBed,doubleBed,singleBed)
        pgTenants(boys,girls,bothPg)
        pgTenants(girls,boys,bothPg)
        pgTenants(bothPg,girls,boys)
        pgFood(mess,selfCooking)
        pgFood(selfCooking,mess)
        pgAmenities(chipAmenitiesPg,amenitiesListPg!!)
        pgAmenities(chipRoomFacilitiesPg,roomFacilitiesListPg!!)

        //marriage hall
//        priceModel(perDay,perPlate,linearPerDayModel,linearPerPlateModel)
//        priceModel(perPlate,perDay,linearPerPlateModel,linearPerDayModel)
        roomTypeMarriageHall(ac,nonAc)
        roomTypeMarriageHall(nonAc,ac)
        marriageHallAmenities(chipAmenitiesMarriageHall,amenitiesListMarriageHall!!)
        marriageHallAmenities(chipEventType,eventTypeList!!)


        //commercial space
        commercialPropertyType(shop,showRoom,wareHouse,coachingCenter,office)
        commercialPropertyType(showRoom,shop,wareHouse,coachingCenter,office)
        commercialPropertyType(wareHouse,showRoom,shop,coachingCenter,office)
        commercialPropertyType(coachingCenter,showRoom,wareHouse,shop,office)
        commercialPropertyType(office,showRoom,wareHouse,coachingCenter,shop)
        commercialFurnishing(furnishedCommercial,semiFurnishedCommercial,unFurnishedCommercial)
        commercialFurnishing(semiFurnishedCommercial,furnishedCommercial,unFurnishedCommercial)
        commercialFurnishing(unFurnishedCommercial,semiFurnishedCommercial,furnishedCommercial)
        commercialAmenities(chipAmenitiesCommercial,amenitiesListCommercial!!)

        //villa
        bedRoomCountVilla(chipBedRoom)
        hallCountVilla(chipHall)
        pujaRoomCountVilla(chipPujaRoomcount)
        tenantsVilla(chipTenants)
        bathRoomCountVilla(chipBathroom)
        villaFurnishing(furnishedVilla,unFurnishedVilla,semiFurnishedVilla)
        villaFurnishing(semiFurnishedVilla,unFurnishedVilla,furnishedVilla)
        villaFurnishing(unFurnishedVilla,furnishedVilla,semiFurnishedVilla)
        villaAmenities(chipAmenitiesVilla,amenitiesListVilla!!)

        btnFilter.setOnClickListener {
            if (!TextUtils.isEmpty(city.editText!!.text.toString())) {
                val linearList = arrayListOf(linearSimpleFlat, linearMarriageHall, linearVilla, linearPg, linearCommercialSpace)
                when {
                    linearList[0].isVisible -> {
//                    Toast.makeText(activity as Context,bedRoomType+tenants+bathroom+propertyType+furnishing,Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity as Context, FlatActivity::class.java)
                        val linearListFlat = arrayListOf(bedRoomType, tenants, bathroom, propertyType, furnishing)
                        val linearListFlat1 = arrayListOf("bedRoomType", "tenants", "bathroom", "propertyType", "furnishing")
                        intent.putExtra(linearListFlat1[0], linearListFlat[0])
                        intent.putExtra(linearListFlat1[1], linearListFlat[1])
                        intent.putExtra(linearListFlat1[2], linearListFlat[2])
                        intent.putExtra(linearListFlat1[3], linearListFlat[3])
                        intent.putExtra(linearListFlat1[4], linearListFlat[4])
                        intent.putExtra("amenitiesListSimpleFlat", amenitiesListSimpleFlat)
                        intent.putExtra("city",city.editText!!.text.toString())
                        intent.putExtra("minBudget",etMinRent.editText!!.text.toString())
                        intent.putExtra("maxBudget",etMaxRent.editText!!.text.toString())
                        startActivity(intent)

                    }
                    linearList[1].isVisible -> {
//                    Toast.makeText(activity as Context,propertyType+priceModel+marriageHallRomType,Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity as Context, MarriageHallRenterActivity::class.java)
//                        val vegMinBudget1 = vegMinBudget.editText!!.text.toString()
//                        val vegMaxBudget1 = vegMaxBudget.editText!!.text.toString()
//                        val nonVegMinBudget1 = nonVegMinBudget.editText!!.text.toString()
//                        val nonVegMaxBudget1 = nonVegMaxBudget.editText!!.text.toString()
//                        val perDayMaxBudget1 = perDayMaxBudget.editText!!.text.toString()
//                        val perDayMinBudget1 = perDayMinBudget.editText!!.text.toString()
                        val maxCapacity1 = maxCapacity.editText!!.text.toString()
                        intent.putExtra("propertyType", propertyType)
//                        intent.putExtra("priceModel", priceModel)
                        intent.putExtra("marriageHallRoomType", marriageHallRomType)
//                        intent.putExtra("vegMinBudget", vegMinBudget1)
//                        intent.putExtra("vegMaxBudget", vegMaxBudget1)
//                        intent.putExtra("nonVegMinBudget", nonVegMinBudget1)
//                        intent.putExtra("nonVegMaxBudget", nonVegMaxBudget1)
//                        intent.putExtra("perDayMinBudget", perDayMinBudget1)
//                        intent.putExtra("perDayMaxBudget", perDayMaxBudget1)
                        intent.putExtra("maxCapacity", maxCapacity1)
                        intent.putExtra("city", city.editText!!.text.toString())
                        intent.putExtra("amenitiesList",amenitiesListMarriageHall)
                        intent.putExtra("eventType",eventTypeList)
                        startActivity(intent)
                    }
                    linearList[2].isVisible -> {
                        Toast.makeText(activity as Context, hallVilla + pujaroomVilla + bedRoomVilla + tenantsVilla + bathRoomVilla + propertyType + furnishingVilla, Toast.LENGTH_SHORT).show()
                        val intent=Intent(activity as Context,VillaRenterActivity::class.java)
                        intent.putExtra("city",city.editText!!.text.toString())
                        intent.putExtra("minBudget",etMinRent.editText!!.text.toString())
                        intent.putExtra("maxBudget",etMaxRent.editText!!.text.toString())
                        intent.putExtra("propertyType",propertyType)
                        intent.putExtra("bedRoomType",bedRoomVilla)
                        intent.putExtra("hallCount",hallVilla)
                        intent.putExtra("pujaRoom",pujaroomVilla)
                        intent.putExtra("tenants",tenantsVilla)
                        intent.putExtra("amenitiesList",amenitiesListVilla)
                        intent.putExtra("furnishing",furnishingVilla)
                        intent.putExtra("bathroomCount",bathRoomVilla)
                        startActivity(intent)
                    }
                    linearList[3].isVisible -> {
//                        Toast.makeText(activity as Context, bedRoomTypePg + tenantsPg + foodPg + propertyType, Toast.LENGTH_SHORT).show()
                        val intent=Intent(activity as Context,PgRenterActivity::class.java)
                        intent.putExtra("city",city.editText!!.text.toString())
                        intent.putExtra("minBudget",etMinRent.editText!!.text.toString())
                        intent.putExtra("maxBudget",etMaxRent.editText!!.text.toString())
                        intent.putExtra("propertyType",propertyType)
                        intent.putExtra("bedRoomType",bedRoomTypePg)
                        intent.putExtra("tenants",tenantsPg)
                        intent.putExtra("foodPg",foodPg)
                        intent.putExtra("roomFacilities",roomFacilitiesListPg)
                        intent.putExtra("amenitiesList",amenitiesListPg)
                        startActivity(intent)
                    }
                    linearList[4].isVisible -> {
                        Toast.makeText(activity as Context, propertyTypeCommercial + propertyType + furnishingCommercial+pujaroomVilla, Toast.LENGTH_SHORT).show()
                        val intent=Intent(activity as Context,CommercialSpaceActivity::class.java)
                        intent.putExtra("city",city.editText!!.text.toString())
                        intent.putExtra("minBudget",etMinRent.editText!!.text.toString())
                        intent.putExtra("maxBudget",etMaxRent.editText!!.text.toString())
                        intent.putExtra("propertyType",propertyType)
                        intent.putExtra("propertyUse",propertyTypeCommercial)
                        intent.putExtra("minDistance",minDistance.editText!!.text.toString())
                        intent.putExtra("maxDistance",maxDistance.editText!!.text.toString())
                        intent.putExtra("furnishing",furnishingVilla)
                        intent.putExtra("amenitiseList",amenitiesListCommercial)
                    }
                }
            }else{
                Toast.makeText(activity as Context,"Please select city",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun propertyTypeUi(txtView1:TextView,txtView2: TextView,txtView3:TextView,txtView4: TextView,
                               txtView5:TextView,txtView6: TextView,txtView7:TextView,txtView8: TextView,
                               txtView9:TextView,txtView10:TextView,linear1:LinearLayout,linear2:LinearLayout,
                               linear3:LinearLayout,linear4:LinearLayout,linear5:LinearLayout){
        txtView1.setOnClickListener {
            txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
            txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
            linear1.visibility=View.VISIBLE
            propertyType=txtView1.text.toString()
            if (txtView1==marriageHall){
                linearBudget.visibility=View.GONE
                txtBudget.visibility=View.GONE
            }else{
                linearBudget.visibility=View.VISIBLE
                txtBudget.visibility=View.VISIBLE
            }
            txtView2.setBackgroundResource(R.drawable.property_type_background)
            txtView3.setBackgroundResource(R.drawable.property_type_background)
            txtView4.setBackgroundResource(R.drawable.property_type_background)
            txtView5.setBackgroundResource(R.drawable.property_type_background)
            txtView6.setBackgroundResource(R.drawable.property_type_background)
            txtView7.setBackgroundResource(R.drawable.property_type_background)
            txtView8.setBackgroundResource(R.drawable.property_type_background)
            txtView9.setBackgroundResource(R.drawable.property_type_background)
            txtView10.setBackgroundResource(R.drawable.property_type_background)
            txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView4.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView5.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView6.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView7.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView8.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView9.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            txtView10.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            linear2.visibility=View.GONE
            linear3.visibility=View.GONE
            linear4.visibility=View.GONE
            linear5.visibility=View.GONE
        }
    }

    //simple flat
    private fun simpleFlatBedroom(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (bedRoomType!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                bedRoomType=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                bedRoomType=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun simpleFlatTenants(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (tenants!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                tenants=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                tenants=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun simpleFlatBathroom(txtView1:TextView,txtView2: TextView,txtView3:TextView,txtView4: TextView,
                               txtView5:TextView,txtView6: TextView){
        txtView1.setOnClickListener {
            if (bathroom!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                bathroom=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView4.setBackgroundResource(R.drawable.property_type_background)
                txtView5.setBackgroundResource(R.drawable.property_type_background)
                txtView6.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView4.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView5.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView6.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                bathroom=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView4.setBackgroundResource(R.drawable.property_type_background)
                txtView5.setBackgroundResource(R.drawable.property_type_background)
                txtView6.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView4.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView5.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView6.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }
        }
    }
    private fun simpleFlatFurnishing(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (furnishing!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                furnishing=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                furnishing=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun simpleFlatAmenities(chipGroup: ChipGroup,tagList:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tagList.add(it.text.toString())
                    }else{
                        tagList.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    //pg
    private fun pgBedroom(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (bedRoomTypePg!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                bedRoomTypePg=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                bedRoomTypePg=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun pgTenants(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (tenantsPg!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                tenantsPg=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                tenantsPg=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun pgFood(txtView1:TextView,txtView2: TextView){
        txtView1.setOnClickListener {
            if (foodPg!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                foodPg=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                foodPg=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun pgAmenities(chipGroup: ChipGroup,tagList:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tagList.add(it.text.toString())
                    }else{
                        tagList.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    //marriage hall
//    private fun priceModel(textView1:TextView,textView2: TextView,linear1: LinearLayout,linear2: LinearLayout){
//        textView1.setOnClickListener {
//            if (priceModel!=textView1.text.toString()){
//                textView1.setBackgroundResource(R.drawable.selected_property_type_background)
//                textView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
//                priceModel=textView1.text.toString()
//                textView2.setBackgroundResource(R.drawable.property_type_background)
//                textView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
//                linear1.visibility=View.VISIBLE
//                linear2.visibility=View.GONE
//            }else{
//                priceModel=""
//                textView1.setBackgroundResource(R.drawable.property_type_background)
//                textView2.setBackgroundResource(R.drawable.property_type_background)
//                textView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
//                textView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
//                linear1.visibility=View.GONE
//                linear2.visibility=View.GONE
//            }
//
//        }
//    }
    private fun roomTypeMarriageHall(txtView1:TextView,txtView2: TextView){
        txtView1.setOnClickListener {
            if (marriageHallRomType!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                marriageHallRomType=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                marriageHallRomType=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun marriageHallAmenities(chipGroup: ChipGroup,tagList:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tagList.add(it.text.toString())
                    }else{
                        tagList.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    //commercial spaces
    private fun commercialPropertyType(txtView1:TextView,txtView2: TextView,txtView3:TextView,txtView4: TextView,
                                   txtView5:TextView){
        txtView1.setOnClickListener {
            if (propertyTypeCommercial!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                propertyTypeCommercial=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView4.setBackgroundResource(R.drawable.property_type_background)
                txtView5.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView4.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView5.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                propertyTypeCommercial=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView4.setBackgroundResource(R.drawable.property_type_background)
                txtView5.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView4.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView5.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun commercialFurnishing(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (furnishingCommercial!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                furnishingCommercial=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                furnishingCommercial=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun commercialAmenities(chipGroup: ChipGroup,tagList:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tagList.add(it.text.toString())
                    }else{
                        tagList.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    //villa
    private fun bedRoomCountVilla(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        bedRoomVilla=it.text.toString()
                    }else{
                        bedRoomVilla=""
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun hallCountVilla(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    hallVilla = if (isChecked) {
                        it.text.toString()
                    }else{
                        ""
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun pujaRoomCountVilla(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        pujaroomVilla=it.text.toString()
                    }else{
                        pujaroomVilla=""
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun tenantsVilla(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tenantsVilla=it.text.toString()
                    }else{
                        tenantsVilla=""
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun bathRoomCountVilla(chipGroup: ChipGroup) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        bathRoomVilla=it.text.toString()
                        Toast.makeText(activity as Context,bathRoomVilla,Toast.LENGTH_SHORT).show()
                    }else{
                        bathRoomVilla=""
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }
    private fun villaFurnishing(txtView1:TextView,txtView2: TextView,txtView3:TextView){
        txtView1.setOnClickListener {
            if (furnishingVilla!=txtView1.text.toString()){
                txtView1.setBackgroundResource(R.drawable.selected_property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.white))
                furnishingVilla=txtView1.text.toString()
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }else{
                furnishingVilla=""
                txtView1.setBackgroundResource(R.drawable.property_type_background)
                txtView2.setBackgroundResource(R.drawable.property_type_background)
                txtView3.setBackgroundResource(R.drawable.property_type_background)
                txtView1.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView2.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
                txtView3.setTextColor(ContextCompat.getColor(activity as Context,R.color.black))
            }

        }
    }
    private fun villaAmenities(chipGroup: ChipGroup,tagList:ArrayList<String>) {
        chipGroup.children.forEach {
            try {
                (it as Chip).setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tagList.add(it.text.toString())
                    }else{
                        tagList.remove(it.text.toString())
                    }
                }
            } catch (e: TypeCastException) {
                e.printStackTrace()
            }
        }

    }

    private fun getCurrentLocation(etCity:TextInputLayout){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as Context)
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 2000
        locationRequest.interval = 4000
        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(this)
                if (p0 != null && p0.locations.size > 0) {
                    try {

                        val latestLocationIndex = p0.locations.size - 1
                        latitude = p0.locations[latestLocationIndex].latitude
                        longitude = p0.locations[latestLocationIndex].longitude
                        val gc = Geocoder(activity as Context, Locale.getDefault())
                        try {
                            val addresses: List<Address> = gc.getFromLocation(latitude,
                                    longitude, 1)
                            val fullAddress = addresses[0].getAddressLine(0)
                            val city = addresses[0].locality

                            etCity.editText!!.setText(city)
                        }catch (e: IOException){
                            Toast.makeText(activity as Context,"Failed to get location", Toast.LENGTH_SHORT).show()
                        }

//                      txt_location.text=" ${address.getAddressLine(0)},${address.locality}"
                    } catch (e: InvocationTargetException) {
                        e.targetException
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity as Context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    Activity(),
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
                        status.startResolutionForResult(this.requireActivity(), REQUEST_CHECK_SETTINGS)
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
                                activity as Context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                                activity as Context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                            activity as Context, "permission allowed",
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
                            activity as Context, "some error occurred",
                            Toast.LENGTH_SHORT
                    ).show()
                    return
                }

            } else {
                Toast.makeText(
                        activity as Context, "Permission Denied",
                        Toast.LENGTH_SHORT
                ).show()
                return
            }

        }

    }


}

