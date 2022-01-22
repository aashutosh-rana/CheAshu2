package com.bcebhagalpur.cheashu.dashboard.renter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.MarriageHallAdapter
import com.bcebhagalpur.cheashu.dashboard.model.MarriageHallListModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MarriageHallRenterActivity : AppCompatActivity() {

    private lateinit var txtItemNotFound: TextView
    private lateinit var relativeProgress: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerMarriageHall: RecyclerView
    private lateinit var etSearch:SearchView
    lateinit var marriageHallAdapter: MarriageHallAdapter
    private val marriageHallListModel = ArrayList<MarriageHallListModel>()
    val db = FirebaseFirestore.getInstance()
//    private var queryListMarriageHall:ArrayList<String>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marriage_hall_renter)

        txtItemNotFound=findViewById(R.id.txtItemNotFound)
        relativeProgress=findViewById(R.id.relativeProgress)
        progressBar=findViewById(R.id.progressBar)
        recyclerMarriageHall=findViewById(R.id.recyclerMarriageHall)
        etSearch=findViewById(R.id.etSearch)
        txtItemNotFound.visibility=View.GONE
        relativeProgress.visibility=View.VISIBLE
        marriageHallAdapter= MarriageHallAdapter(this,marriageHallListModel,this)

        val layoutManager = LinearLayoutManager(this)
        recyclerMarriageHall.layoutManager = layoutManager
        recyclerMarriageHall.adapter =marriageHallAdapter
        recyclerMarriageHall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

//        val propertyType=intent.getStringExtra("propertyType")
        val marriageHallRoomType=intent.getStringExtra("marriageHallRoomType")
        val maxCapacity=intent.getStringExtra("maxCapacity")
        val city=intent.getStringExtra("city")
        val amenitiesList=intent.getStringArrayListExtra("amenitiesList")
        val eventType=intent.getStringArrayListExtra("eventType")

        if (eventType!!.size>0)
        {
            if (amenitiesList!!.size>0)
            {
                if (marriageHallRoomType!="" && maxCapacity==""){
                    if (marriageHallRoomType=="Ac room"){
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("acRoom","")
                                .whereArrayContainsAny("tags",amenitiesList)
                                .whereArrayContainsAny("suitableFor",eventType)
                        getData(query)
                    }else {
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("nonAcRoom","")
                                .whereArrayContainsAny("tags",amenitiesList)
                                .whereArrayContainsAny("suitableFor",eventType)
                        getData(query)
                    }

                }else if (marriageHallRoomType=="" && maxCapacity!=""){
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("tags",amenitiesList)
                            .whereArrayContainsAny("suitableFor",eventType)
                            .whereGreaterThanOrEqualTo("capacity",maxCapacity!!)
                    getData(query)
                }else{
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("tags",amenitiesList)
                            .whereArrayContainsAny("suitableFor",eventType)
                    getData(query)
                }
            }
            else{
                if (marriageHallRoomType!="" && maxCapacity==""){
                    if (marriageHallRoomType=="Ac room"){
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereArrayContainsAny("suitableFor",eventType)
                                .whereNotEqualTo("acRoom","")
                        getData(query)
                    }else {
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereArrayContainsAny("suitableFor",eventType)
                                .whereNotEqualTo("nonAcRoom","")
                        getData(query)
                    }

                }else if (marriageHallRoomType=="" && maxCapacity!=""){
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("suitableFor",eventType)
                            .whereGreaterThanOrEqualTo("capacity",maxCapacity!!)
                    getData(query)
                }else{
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("suitableFor",eventType)
                    getData(query)
                }
            }
        }else{
            if (amenitiesList!!.size>0)
            {
                if (marriageHallRoomType!="" && maxCapacity==""){
                    if (marriageHallRoomType=="Ac room"){
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("acRoom","")
                                .whereArrayContainsAny("tags",amenitiesList)
                        getData(query)
                    }else {
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("nonAcRoom","")
                                .whereArrayContainsAny("tags",amenitiesList)
                        getData(query)
                    }

                }else if (marriageHallRoomType=="" && maxCapacity!=""){
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("tags",amenitiesList)
                            .whereGreaterThanOrEqualTo("capacity",maxCapacity!!)
                    getData(query)
                }else{
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereArrayContainsAny("tags",amenitiesList)
                    getData(query)
                }
            }
            else{
                if (marriageHallRoomType!="" && maxCapacity==""){
                    if (marriageHallRoomType=="Ac room"){
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("acRoom","")
                        getData(query)
                    }else {
                        val query=db.collection("MarriageHall").whereEqualTo("city",city)
                                .whereNotEqualTo("nonAcRoom","")
                        getData(query)
                    }

                }else if (marriageHallRoomType=="" && maxCapacity!=""){
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                            .whereGreaterThanOrEqualTo("capacity",maxCapacity!!)
                    getData(query)
                }else{
                    val query=db.collection("MarriageHall").whereEqualTo("city",city)
                    getData(query)
                }
            }
        }

        searchCity()

    }

    private fun getData(query: Query){
        query.get().addOnSuccessListener { p0 ->
            if (!p0!!.isEmpty) {
                relativeProgress.visibility= View.GONE
                for (snapshot in p0.documents) {
                    val s2 = snapshot.toObject(MarriageHallListModel::class.java)
                    marriageHallListModel.add(s2!!)
                }
                recyclerMarriageHall.adapter = marriageHallAdapter
                marriageHallAdapter.notifyDataSetChanged()
            } else {
                progressBar.visibility= View.GONE
                txtItemNotFound.visibility= View.VISIBLE
            }
        }
    }
    private fun searchCity(){
        etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                marriageHallAdapter.getFilter().filter(newText)
                return false
            }

        })
    }
}
