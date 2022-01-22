package com.bcebhagalpur.cheashu.drawer.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.ExploreItemListAdapter
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyUploadsActivity : AppCompatActivity() {

    private lateinit var recyclerMyUploads: RecyclerView
    private lateinit var progressBar: ProgressBar
    lateinit var adapter: ExploreItemListAdapter
    val flatOwner = ArrayList<ExploreItemListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_uploads)

        recyclerMyUploads=findViewById(R.id.recyclerMyUploads)
        progressBar=findViewById(R.id.progressBar)
        adapter= ExploreItemListAdapter(this,flatOwner,MyUploadsActivity())

        val layoutManager = LinearLayoutManager(this)
        recyclerMyUploads.layoutManager = layoutManager
        recyclerMyUploads.adapter = adapter
        recyclerMyUploads.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        getUser()
    }

    private fun getUser() {
        val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val ref1=db.collection("Property").document("Apartment")

//        db.collection("Property").document("Apartment").get().addOnSuccessListener {
//
//        }

        db.collection("All").whereEqualTo("id",currentUser).get().addOnSuccessListener { p0 ->
            if (!p0!!.isEmpty) {
                progressBar.visibility = View.GONE
                for (snapshot in p0.documents) {
                    val s2=snapshot.toObject(ExploreItemListModel::class.java)
                    flatOwner.add(s2!!)
                }
                recyclerMyUploads.adapter = adapter
                adapter.notifyDataSetChanged()
            } else {

            }
        }

//       db..addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userList = ArrayList<BachelorUser>()
//                progressLayout.visibility=View.GONE
//
//                if (snapshot.exists()) {
//                    for (p0 in snapshot.children) {
//                        for (s0 in p0.children){
//                            val p1 = s0.getValue(BachelorUser::class.java)
//                            userList.add(0, p1!!)
//                        }
//                    }
//                    adapter = BachelorAdapter(activity as Context, userList)
//                    if (snapshot.exists()) {
//                        val maxId = snapshot.childrenCount
//                        val maxInt = maxId.toInt()
//                        for (i in 0..maxInt) {
//                            adapter.notifyItemInserted(i)
//                            recyclerBachelor.smoothScrollToPosition(i)
//                        }
//
//                    }
//
//                    recyclerBachelor.adapter = adapter
//
//                } else {
//                    txtItemNotFound.visibility=View.VISIBLE
//                }
//            }
//
//        })
    }
}