package com.bcebhagalpur.cheashu.dashboard.renter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.ExploreItemListAdapter
import com.bcebhagalpur.cheashu.dashboard.adapter.FlatListAdapter
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.bcebhagalpur.cheashu.dashboard.model.FlatItemListModel
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FlatActivity : AppCompatActivity() {

    private lateinit var txtItemNotFound:TextView
    private lateinit var relativeProgress:RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerFlat:RecyclerView
    private lateinit var etSearch:SearchView
    lateinit var flatListAdapter: FlatListAdapter
    val flatListModel = ArrayList<FlatItemListModel>()
    val db = FirebaseFirestore.getInstance()
    private var queryListSimpleFlat:ArrayList<String>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flat)

        txtItemNotFound=findViewById(R.id.txtItemNotFound)
        relativeProgress=findViewById(R.id.relativeProgress)
        progressBar=findViewById(R.id.progressBar)
        recyclerFlat=findViewById(R.id.recyclerFlat)
        etSearch=findViewById(R.id.etSearch)
        txtItemNotFound.visibility=View.GONE
        relativeProgress.visibility=View.VISIBLE

        flatListAdapter= FlatListAdapter(this,flatListModel,this)

        val layoutManager = LinearLayoutManager(this)
        recyclerFlat.layoutManager = layoutManager
        recyclerFlat.adapter =flatListAdapter
        recyclerFlat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        val propertyType=intent.getStringExtra("propertyType")
        val linearListFlat1= arrayListOf("bedRoomType","tenants","bathroom","furnishing")
            val bedRoomType=intent.getStringExtra(linearListFlat1[0])
            val tenants=intent.getStringExtra(linearListFlat1[1])
            val bathroom=intent.getStringExtra(linearListFlat1[2])
            val furnishing=intent.getStringExtra(linearListFlat1[3])
            val amenities=intent.getStringArrayListExtra("amenitiesListSimpleFlat")
            val city=intent.getStringExtra("city")
            val minBudget=intent.getStringExtra("minBudget")
            val maxBudget=intent.getStringExtra("maxBudget")
            val queryList= arrayListOf(bedRoomType,tenants,furnishing,bathroom)

        for (i in  queryList){
            if (i!=""){
                queryListSimpleFlat!!.add(i!!)
            }
        }

        //        val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType).whereEqualTo("furnishingStatus",furnishing)
//                .whereEqualTo("tenants",tenants)
//        getData(query)

        if (minBudget!="" && maxBudget==""){
            if (amenities!!.size>0){
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereArrayContainsAny("amenities",amenities)
                                    .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }
            }
            else{
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!)
                            .whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereEqualTo("tenants",tenants)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("tenants",tenants).whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                    getData(query)
                }
            }
        }else if (maxBudget!="" && minBudget==""){
            if (amenities!!.size>0){
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereEqualTo("tenants",tenants).
                            whereArrayContainsAny("amenities",amenities)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereArrayContainsAny("amenities",amenities) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereArrayContainsAny("amenities",amenities)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereArrayContainsAny("amenities",amenities) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
            }
            else{
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("tenants",tenants)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants)
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city) .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
            }
        }else if (minBudget!="" && maxBudget!=""){
            if (amenities!!.size>0){
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereEqualTo("tenants",tenants)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereArrayContainsAny("amenities",amenities)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
            }
            else{
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("propertySubType",bedRoomType!!)
                            .whereEqualTo("tenants",tenants)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("tenants",tenants)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereGreaterThanOrEqualTo("monthlyRent",minBudget!!.toDouble())
                            .whereLessThanOrEqualTo("monthlyRent",maxBudget!!.toDouble())
                    getData(query)
                }
            }
        }else{
            if (amenities!!.size>0){
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("tenants",tenants).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereArrayContainsAny("amenities",amenities)
                    getData(query)
                }
            }
            else{
                if (bedRoomType!="" && tenants=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!)
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing=="" ){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                    getData(query)
                }else if (tenants!="" && bedRoomType!="" && furnishing!=""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!).whereEqualTo("tenants",tenants)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                    getData(query)
                }else if (tenants!="" && bedRoomType=="" && furnishing==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("tenants",tenants)
                    getData(query)
                }else if (furnishing!="" && tenants=="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!)
                    getData(query)
                }else if (furnishing!="" && tenants!="" && bedRoomType==""){
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                            .whereEqualTo("furnishingStatus",furnishing!!).whereEqualTo("tenants",tenants)
                    getData(query)
                }
                else{
                    val query=db.collection(propertyType!!).whereEqualTo("city",city)
                    getData(query)
                }
            }
            searchCity()
        }

    }

    private fun getData(query:Query){
        query.get().addOnSuccessListener { p0 ->
            if (!p0!!.isEmpty) {
                relativeProgress.visibility=View.GONE
                for (snapshot in p0.documents) {
                    val s2 = snapshot.toObject(FlatItemListModel::class.java)
                    flatListModel.add(s2!!)
                }
                recyclerFlat.adapter = flatListAdapter
                flatListAdapter.notifyDataSetChanged()
            } else {
                progressBar.visibility=View.GONE
                txtItemNotFound.visibility=View.VISIBLE
            }
        }
    }
    private fun searchCity(){
        etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               flatListAdapter.getFilter().filter(newText)
                return false
            }

        })
    }
}

//if (bedRoomType!=""){
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",bedRoomType!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!=""){
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType).whereEqualTo("tenants",tenants!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0 && minBudget!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//            .whereGreaterThanOrEqualTo("rentPerMonth",minBudget!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0 && minBudget!="" && maxBudget!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//            .whereGreaterThanOrEqualTo("rentPerMonth",minBudget!!).whereLessThanOrEqualTo("rentPerMonth",maxBudget!!)
//    getData(query)
//}else if (tenants!=""){
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType",tenants)
//    getData(query)
//}else if (bedRoomType!="" && tenants!=""){
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType).whereEqualTo("tenants",tenants!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0 && minBudget!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//            .whereGreaterThanOrEqualTo("rentPerMonth",minBudget!!)
//    getData(query)
//}else if (bedRoomType!="" && tenants!="" && bathroom!="" && furnishing!="" && amenitiesList!!.size>0 && minBudget!="" && maxBudget!=""){
//    val bathroomCount=bathroom!!.toInt()
//    val query=db.collection(propertyType!!).whereEqualTo("city",city).whereEqualTo("propertySubType", bedRoomType)
//            .whereEqualTo("tenants",tenants!!).whereGreaterThanOrEqualTo("bathroomCount",bathroomCount)
//            .whereEqualTo("furnishing",furnishing!!).whereArrayContainsAny("amenities",amenitiesList)
//            .whereGreaterThanOrEqualTo("rentPerMonth",minBudget!!).whereLessThanOrEqualTo("rentPerMonth",maxBudget!!)
//    getData(query)
//}