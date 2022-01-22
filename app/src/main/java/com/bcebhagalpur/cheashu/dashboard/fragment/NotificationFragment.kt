package com.bcebhagalpur.cheashu.dashboard.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.NotificationAdapter
import com.bcebhagalpur.cheashu.dashboard.model.NotificationList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationFragment : Fragment() {

    private lateinit var relativeProgress:RelativeLayout
    private lateinit var recyclerNotification:RecyclerView
    private lateinit var etSearch: SearchView
    private lateinit var adapter: NotificationAdapter
    private lateinit var txtNotificationNotFound: TextView
    private val notificationList = ArrayList<NotificationList>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
       val view= inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerNotification=view.findViewById(R.id.recyclerNotification)
        relativeProgress=view.findViewById(R.id.relativeProgress)
        etSearch=view.findViewById(R.id.etSearch)
        adapter= NotificationAdapter(activity as Context,notificationList)
        txtNotificationNotFound=view.findViewById(R.id.txtNotificationNotFound)


        relativeProgress.visibility=View.VISIBLE
        val layoutManager = LinearLayoutManager(activity as Context)
        layoutManager.reverseLayout
        layoutManager.stackFromEnd
        recyclerNotification.layoutManager = layoutManager
        val dividerItemDecoration =
                DividerItemDecoration(recyclerNotification.context, layoutManager.orientation)
        recyclerNotification.addItemDecoration(dividerItemDecoration)
       recyclerNotification.adapter = adapter
        recyclerNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        getUser()
        searchCity()

        return view

    }

    private fun searchCity(){
        etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return false
            }

        })
    }
    private fun getUser() {

        try {
            val currentUser=FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()
//            val notificationPath="Notifications/${currentUser}/data"

            db.collection("Notifications").document(currentUser).collection("data").get().addOnSuccessListener { p0 ->
                if (!p0!!.isEmpty) {
                    relativeProgress.visibility = View.GONE
                    for (snapshot in p0.documents) {
                        val s2=snapshot.toObject(NotificationList::class.java)
                        notificationList.add(s2!!)
                    }
                   recyclerNotification.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    relativeProgress.visibility = View.GONE
                    txtNotificationNotFound.visibility=View.VISIBLE
                }
            }
        }catch (e:TypeCastException){
            e.printStackTrace()
        }
    }



}