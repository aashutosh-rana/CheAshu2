package com.bcebhagalpur.cheashu.drawer.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.adapter.ExploreItemListAdapter
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerBookmark: RecyclerView
    private lateinit var progressBar: ProgressBar
    lateinit var adapter: ExploreItemListAdapter
    private val flatOwner = ArrayList<ExploreItemListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        recyclerBookmark=findViewById(R.id.recyclerBookmark)
        progressBar=findViewById(R.id.progressBar)
        adapter= ExploreItemListAdapter(this,flatOwner,BookmarkActivity())

        val layoutManager = LinearLayoutManager(this)
       recyclerBookmark.layoutManager = layoutManager
        recyclerBookmark.adapter = adapter
        recyclerBookmark.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })


        val currentUser= FirebaseAuth.getInstance().currentUser!!.uid

        try {
            val db= FirebaseFirestore.getInstance()
            val favouritePath="Bookmarks/$currentUser/data"
            db.collection(favouritePath).get().addOnSuccessListener { it ->
                if (!it.isEmpty){
                    progressBar.visibility=View.GONE
                    for (snapshot in it.documents){
                        val path=snapshot.get("path").toString()
                        db.document(path).get().addOnSuccessListener { p0->
                            if (p0.exists()){
                                val s1=p0.toObject(ExploreItemListModel::class.java)
                                flatOwner.add(s1!!)
                               recyclerBookmark.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        }


    }
}