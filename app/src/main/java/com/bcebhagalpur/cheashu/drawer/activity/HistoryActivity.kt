package com.bcebhagalpur.cheashu.drawer.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.drawer.adapter.HistoryAdapter
import com.bcebhagalpur.cheashu.drawer.model.HistoryItemListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity : AppCompatActivity() {

    private lateinit var btnDeleteAll: Button
    private lateinit var etSearch: SearchView
    private lateinit var adapter: HistoryAdapter
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var txtItemNotFound: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        btnDeleteAll =findViewById(R.id.btnDeleteAll)
        recyclerHistory = findViewById(R.id.recyclerHistory)
        etSearch =findViewById(R.id.etSearch)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        txtItemNotFound = findViewById(R.id.txtItemNotFound)
        progressLayout.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout
        layoutManager.stackFromEnd
        recyclerHistory.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerHistory.context, layoutManager.orientation)
        recyclerHistory.addItemDecoration(dividerItemDecoration)
        val bachelorUser = ArrayList<HistoryItemListModel>()
        adapter = HistoryAdapter(this, bachelorUser)
        recyclerHistory.adapter = adapter
        recyclerHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        })

        getUser()
        searchCity()
        btnDeleteAll.setOnClickListener {
            try {
                val child = FirebaseDatabase.getInstance().reference.child("History")
               child.child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Your all history deleted", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


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

            val child = FirebaseDatabase.getInstance().reference.child("History")
            val anotherChild = child.child(FirebaseAuth.getInstance().currentUser!!.uid).orderByChild("time")
            anotherChild.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = ArrayList<HistoryItemListModel>()
                    progressLayout.visibility = View.GONE

                    if (snapshot.exists()) {
                        for (p0 in snapshot.children) {
                            val p1 = p0.getValue(HistoryItemListModel::class.java)
                            userList.add(0, p1!!)
                        }
                        try {

                            adapter = HistoryAdapter(this@HistoryActivity, userList)
                            if (snapshot.exists()) {
                                val maxId = snapshot.childrenCount
                                val maxInt = maxId.toInt()
                                for (i in 0..maxInt) {
                                    adapter.notifyItemInserted(i)
                                    recyclerHistory.smoothScrollToPosition(i)
                                }

                            }

                            recyclerHistory.adapter = adapter
                        }catch (e:TypeCastException){
                            e.printStackTrace()
                        }

                    }
                    else {
                        try {
                            Toast.makeText(this@HistoryActivity, "no history found", Toast.LENGTH_SHORT).show()
                            txtItemNotFound.visibility = View.VISIBLE
                        }catch (e:TypeCastException){
                            e.printStackTrace()
                        }
                    }
                }

            })
        }catch (e:TypeCastException){
            e.printStackTrace()
        }
    }


}


