package com.bcebhagalpur.cheashu.dashboard.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.DetailPropertyActivity
import com.bcebhagalpur.cheashu.dashboard.model.NotificationList
import com.bcebhagalpur.cheashu.drawer.model.HistoryItemListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(val context: Context, val notificationList: ArrayList<NotificationList>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    var notificationFilter = ArrayList<NotificationList>()

    init {
        notificationFilter = notificationList
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtPropertySubType: TextView = itemView.findViewById(R.id.txtPropertySubType)
        var imgCall: ImageView = itemView.findViewById(R.id.imgCall)
        var imgWp: ImageView = itemView.findViewById(R.id.imgWp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(context).inflate(R.layout.notification_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property=notificationFilter[position]
        holder.txtPropertySubType.text=property.propertySubType
        holder.imgCall.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 200)
            }
            else{
                val intent1 = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", property.renterNumber, null))
                context.startActivity(intent1)
            }

        }

        holder.imgWp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=${property.renterNumber}"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        }

        val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun getItemCount(): Int {
        return notificationFilter.size
    }

    fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                notificationFilter = if (charSearch.isEmpty()) {
                    notificationList
                } else {
                    val resultList = ArrayList<NotificationList>()
                    for (row in notificationList) {
                        if (row.propertySubType!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = notificationFilter
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                notificationFilter = results!!.values as java.util.ArrayList<NotificationList>
                notifyDataSetChanged()

            }

        }
    }

}
