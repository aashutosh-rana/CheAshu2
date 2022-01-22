package com.bcebhagalpur.cheashu.dashboard.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.ExploreItemListAdapter
import com.bcebhagalpur.cheashu.dashboard.adapter.FlatListAdapter
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.bcebhagalpur.cheashu.dashboard.model.FlatItemListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExploreFragment : Fragment() {

    private lateinit var btnPostProperty:Button
    private lateinit var searchView:androidx.appcompat.widget.SearchView
    private lateinit var relativeApartment:RelativeLayout
    private lateinit var relativeFlat:RelativeLayout
    private lateinit var relativeHouse:RelativeLayout
    private lateinit var relativeFarmHouse:RelativeLayout
    private lateinit var relativeVilla:RelativeLayout
    private lateinit var relativePg:RelativeLayout
    private lateinit var relativeCommercialSpace:RelativeLayout
    private lateinit var relativeHostel:RelativeLayout
    private lateinit var relativeMarriageHall:RelativeLayout
    private lateinit var recyclerSimpleFlat:RecyclerView
    private lateinit var recyclerFlat:RecyclerView
    private lateinit var progressBar:ProgressBar
    lateinit var exploreItemListAdapter: ExploreItemListAdapter
    lateinit var flatListAdapter: FlatListAdapter
    val exploreItemListModel = ArrayList<ExploreItemListModel>()
    val flatListModel = ArrayList<FlatItemListModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    {
        val view= inflater.inflate(R.layout.fragment_explore, container, false)

        btnPostProperty=view.findViewById(R.id.btnPostProperty)
        searchView=view.findViewById(R.id.searchView)
        relativeApartment=view.findViewById(R.id.relativeApartment)
        relativeFlat=view.findViewById(R.id.relativeFlat)
        relativeHouse=view.findViewById(R.id.relativeHouse)
        relativeFarmHouse=view.findViewById(R.id.relativeFarmHouse)
        relativeVilla=view.findViewById(R.id.relativeVilla)
        relativePg=view.findViewById(R.id.relativePg)
        relativeCommercialSpace=view.findViewById(R.id.relativeCommercial)
        relativeHostel=view.findViewById(R.id.relativeHostel)
        relativeMarriageHall=view.findViewById(R.id.relativeMarriageHall)

        recyclerSimpleFlat=view.findViewById(R.id.recyclerSimpleFlat)
        recyclerFlat=view.findViewById(R.id.recyclerFlat)
        progressBar=view.findViewById(R.id.progressBar)
        exploreItemListAdapter= ExploreItemListAdapter(activity as Context,exploreItemListModel,activity as AppCompatActivity)
        flatListAdapter= FlatListAdapter(activity as Context,flatListModel,activity as AppCompatActivity)

        val layoutManager = LinearLayoutManager(activity as Context,LinearLayoutManager.HORIZONTAL,false)
        val layoutManager1 = LinearLayoutManager(activity as Context,LinearLayoutManager.HORIZONTAL,false)
        recyclerSimpleFlat.layoutManager = layoutManager
        recyclerSimpleFlat.adapter = exploreItemListAdapter
        recyclerSimpleFlat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        recyclerFlat.layoutManager = layoutManager1
        recyclerFlat.adapter = exploreItemListAdapter
        recyclerFlat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        btnPostProperty.setOnClickListener {
            val bottomSheet=com.bcebhagalpur.cheashu.dashboard.helper.BottomSheetDialog()
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager,"ModalBottomSheet")
        }
        getUser()
        getUserFlat()
        return view
    }

    private fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val ref1 = db.collection("Property").document("Apartment")

//        db.collection("Property").document("Apartment").get().addOnSuccessListener {
//
//        }

        db.collection("Apartment").get().addOnSuccessListener { p0 ->
            if (!p0!!.isEmpty) {
                progressBar.visibility = View.GONE
                for (snapshot in p0.documents) {
                    val s2 = snapshot.toObject(ExploreItemListModel::class.java)
                    exploreItemListModel.add(s2!!)
                }
                recyclerSimpleFlat.adapter = exploreItemListAdapter
                exploreItemListAdapter.notifyDataSetChanged()
            } else {

            }
        }
    }

    private fun getUserFlat() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("Apartment").get().addOnSuccessListener { p0 ->
            if (!p0!!.isEmpty) {
                progressBar.visibility = View.GONE
                for (snapshot in p0.documents) {
                    val s2 = snapshot.toObject(FlatItemListModel::class.java)
                    flatListModel.add(s2!!)
                }
                recyclerFlat.adapter = flatListAdapter
                flatListAdapter.notifyDataSetChanged()
            } else {

            }
        }
    }

}