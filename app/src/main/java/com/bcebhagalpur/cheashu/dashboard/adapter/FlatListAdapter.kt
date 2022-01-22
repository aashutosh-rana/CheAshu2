package com.bcebhagalpur.cheashu.dashboard.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.DetailPropertyActivity
import com.bcebhagalpur.cheashu.dashboard.model.ExploreItemListModel
import com.bcebhagalpur.cheashu.dashboard.model.FlatItemListModel
import com.bcebhagalpur.cheashu.drawer.model.HistoryItemListModel
import com.bcebhagalpur.cheashu.notification.FcmNotificationsSender
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class FlatListAdapter(
        val context: Context,
        val flatItemList: ArrayList<FlatItemListModel>, val activity: Activity) : RecyclerView.Adapter<FlatListAdapter.MyViewHolder>()
{

    var flatFilter = ArrayList<FlatItemListModel>()

    init {
        flatFilter = flatItemList
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var imgThumbnail: ImageView = itemView.findViewById(R.id.imgFlat)
        var txtRoomRent:TextView = itemView.findViewById(R.id.txtRent)
        var txtCityName:TextView = itemView.findViewById(R.id.txtCity)
        var txtFullAddress:TextView = itemView.findViewById(R.id.txtFullAddress)
        var txtPropertySubType:TextView= itemView.findViewById(R.id.txtPropertySubType)
        var btnCall:Button=itemView.findViewById(R.id.btnCall)
        var descreption:CardView=itemView.findViewById(R.id.description)
        var btnWp:ImageView=itemView.findViewById(R.id.btnWp)
        var imgBookMark:ImageView=itemView.findViewById(R.id.imgBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(context).inflate(
                R.layout.flat_item_list,
                parent,
                false
        )
        return MyViewHolder(itemView)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property=flatFilter[position]
//        holder.txtOwnerName.text=property.ownerName
        val averageRent= property.monthlyRent
        holder.txtRoomRent.text= "$averageRent/month"
        holder.txtCityName.text=property.city
        holder.txtFullAddress.text=property.address
        holder.txtPropertySubType.text=property.propertySubType
        val currentUser=FirebaseAuth.getInstance().currentUser
        val db=FirebaseFirestore.getInstance()
        holder.btnWp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=${property.number}"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
                        val tokenPath="Token/${property.id}"
            val notificationPath="Notifications/${property.id}/data/${property.documentId}"
            val notificationData= hashMapOf("renterId" to currentUser!!.uid, "renterNumber" to currentUser!!.phoneNumber,
                    "propertySubType" to property.propertySubType)
            db.document(tokenPath).get().addOnSuccessListener {
                if (it.exists()){
                    val token=it.get("token").toString()
                    FcmNotificationsSender(token,"A tenants send you request","hello i want your property",context,activity).SendNotifications()
                    db.document(notificationPath).set(notificationData)
                }
            }
        }

        val documentId=property.documentId


        val data= hashMapOf("path" to property.path, "thumbImage" to property.images,"propertyType" to property.propertyType)

        try {
            val favouritePath="Bookmarks/${currentUser!!.uid}/data/$documentId"
            db.document(favouritePath).get().addOnSuccessListener {
                if (!it.exists()){
                    holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_border_rgb_24)
                    holder.imgBookMark.setOnClickListener {
                        db.document(favouritePath).get().addOnSuccessListener {
                            if (!it.exists()){
                                db.document(favouritePath).set(data).addOnSuccessListener {
                                    holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                                    Snackbar.make(
                                            holder.descreption,
                                            "Added to bookmark successfully......",
                                            Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                db.document(favouritePath).delete()
                                holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_border_rgb_24)
                                Snackbar.make(
                                        holder.descreption,
                                        "Removed to bookmark successfully......",
                                        Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }else{
                    holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    holder.imgBookMark.setOnClickListener {
                        db.document(favouritePath).get().addOnSuccessListener {
                            if (!it.exists()){
                                db.document(favouritePath).set(data).addOnSuccessListener {
                                    holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                                    Snackbar.make(
                                            holder.descreption,
                                            "Added to bookmark successfully......",
                                            Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                db.document(favouritePath).delete()
                                holder.imgBookMark.setImageResource(R.drawable.ic_baseline_bookmark_border_rgb_24)
                                Snackbar.make(
                                        holder.descreption,
                                        "Removed to bookmark successfully......",
                                        Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }
            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        }

        if(property.images!=null) {
//            val policy = StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build()
//            StrictMode.setThreadPolicy(policy)
            Picasso.get().load(property.images[0]).fit().error(R.drawable.about_us).into(holder.imgThumbnail)
        }

        holder.descreption.setOnClickListener {
            val student = FirebaseDatabase.getInstance().reference.child("History")
            val anotherChild = student.child(currentUser!!.uid)
            val anotherChild2=anotherChild.child(documentId.toString())
            val currentTime=Calendar.getInstance().time
            anotherChild2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        anotherChild2.child("path").setValue(property.path)
                        anotherChild2.child("address").setValue(property.address)
                        anotherChild2.child("monthlyRent").setValue(property.monthlyRent.toString())
                        anotherChild2.child("propertyType").setValue(property.propertyType)
                        anotherChild2.child("documentId").setValue(property.documentId)
                        anotherChild2.child("time").setValue(ServerValue.TIMESTAMP)
                    } else {
                        anotherChild2.child("time").setValue(ServerValue.TIMESTAMP)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            val intent=Intent(context, DetailPropertyActivity::class.java)
            intent.putExtra("name", property.ownerName)
            intent.putExtra("monthlyRent", property.monthlyRent)
            intent.putExtra("images", property.images)
            intent.putExtra("city", property.city)
            intent.putExtra("path", property.path)
            intent.putExtra("address", property.address)
            intent.putExtra("propertyType", property.propertyType)
            intent.putExtra("latitude", property.latitude)
            intent.putExtra("longitude", property.longitude)
            context.startActivity(intent)
                        val tokenPath="Token/${property.id}"
            val notificationPath="Notifications/${property.id}/data/${property.documentId}"
            val notificationData= hashMapOf("renterId" to currentUser!!.uid, "renterNumber" to currentUser!!.phoneNumber,
                    "propertySubType" to property.propertySubType)
            db.document(tokenPath).get().addOnSuccessListener {
                if (it.exists()){
                    val token=it.get("token").toString()
                    FcmNotificationsSender(token,"A tenants send you request","hello i want your property",context,activity).SendNotifications()
                    db.document(notificationPath).set(notificationData)
                }
            }
        }

        holder.btnCall.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 200)
                }
                else{
                    val intent1 = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", property.number, null))
                    context.startActivity(intent1)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        }

    }

    override fun getItemCount(): Int {
        return flatFilter.size
    }

    fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                flatFilter = if (charSearch.isEmpty()) {
                    flatItemList
                } else {
                    val resultList = ArrayList<FlatItemListModel>()
                    for (row in flatItemList) {
                        if (row.address!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.monthlyRent!!.toString().toLowerCase(Locale.ROOT).contains(charSearch
                                        .toLowerCase(Locale.ROOT)) || row.propertyType!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT))
                                || row.path!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.tenants!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.furnishingStatus!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = flatFilter
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                flatFilter = results!!.values as java.util.ArrayList<FlatItemListModel>
                notifyDataSetChanged()

            }

        }
    }

}