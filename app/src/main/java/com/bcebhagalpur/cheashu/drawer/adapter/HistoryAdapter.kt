package com.bcebhagalpur.cheashu.drawer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.DetailPropertyActivity
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.bcebhagalpur.cheashu.drawer.model.HistoryItemListModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(val context: Context, val historyInfoList: ArrayList<HistoryItemListModel>) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    var historyFilter = ArrayList<HistoryItemListModel>()

    init {
        historyFilter = historyInfoList
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var monthlyRent: TextView = itemView.findViewById(R.id.monthlyRent)
        var propertyType: TextView = itemView.findViewById(R.id.propertyType)
        var fullAddress: TextView = itemView.findViewById(R.id.fullAddress)
        var historyDetail: LinearLayout =itemView.findViewById(R.id.historyDetail)
        var btnDelete:Button=itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(context).inflate(R.layout.history_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property=historyFilter[position]
        holder.monthlyRent.text="${property.monthlyRent}/month"
        holder.fullAddress.text= property.address
        holder.propertyType.text=property.propertyType
        val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        holder.historyDetail.setOnClickListener {
            val intent=Intent(context,DetailPropertyActivity::class.java)
            intent.putExtra("path",property.path)
            context.startActivity(intent)
        }
        holder.btnDelete.setOnClickListener {
            try {
                val child = FirebaseDatabase.getInstance().reference.child("History")
                child.child(FirebaseAuth.getInstance().currentUser!!.uid).child(property.documentId.toString()).removeValue { error, ref ->
                    notifyDataSetChanged()
                }
                Toast.makeText(context,"deleted",Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    override fun getItemCount(): Int {
        return historyFilter.size
    }

    fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                historyFilter = if (charSearch.isEmpty()) {
                    historyInfoList
                } else {
                    val resultList = ArrayList<HistoryItemListModel>()
                    for (row in historyInfoList) {
                        if (row.address!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT)) || row.monthlyRent!!.toString().toLowerCase(Locale.ROOT).contains(charSearch
                                .toLowerCase(Locale.ROOT)) || row.propertyType!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))
                            || row.path!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = historyFilter
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                historyFilter = results!!.values as java.util.ArrayList<HistoryItemListModel>
                notifyDataSetChanged()

            }

        }
    }

}
