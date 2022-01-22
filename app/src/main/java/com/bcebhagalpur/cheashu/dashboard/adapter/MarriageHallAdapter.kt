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
import android.widget.Button
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.DetailPropertyActivity
import com.bcebhagalpur.cheashu.dashboard.activity.MarriageHallDetailActivity
import com.bcebhagalpur.cheashu.dashboard.model.MarriageHallListModel
import com.bcebhagalpur.cheashu.notification.FcmNotificationsSender
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class MarriageHallAdapter(
        val context: Context,
        val marriageHallItemList: ArrayList<MarriageHallListModel>, val activity: Activity) : RecyclerView.Adapter<MarriageHallAdapter.MyViewHolder>()
{

    var marriageHallFilter = ArrayList<MarriageHallListModel>()

    init {
        marriageHallFilter = marriageHallItemList
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imgThumbnail: ImageView = itemView.findViewById(R.id.imgMarriageHall)
        var txtHallName: TextView = itemView.findViewById(R.id.txtHallName)
        var txtCityName: TextView = itemView.findViewById(R.id.txtCityName)
        var txtFullAddress: TextView = itemView.findViewById(R.id.txtFullAddress)
        var btnLocation: Button =itemView.findViewById(R.id.btnShowLocation)
        var descreption: CardView =itemView.findViewById(R.id.detailMarriageHall)
        var btnEnquiry: Button =itemView.findViewById(R.id.btnEnquiry)
        var imgBookMark: ImageView =itemView.findViewById(R.id.imgBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(context).inflate(
                R.layout.marriagehall_item_list,
                parent,
                false
        )
        return MyViewHolder(itemView)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val marriageHall=marriageHallFilter[position]
//        holder.txtOwnerName.text=property.ownerName
        holder.txtHallName.text= marriageHall.hallName
        holder.txtCityName.text=marriageHall.city
        holder.txtFullAddress.text=marriageHall.fullAddress
        val currentUser= FirebaseAuth.getInstance().currentUser
        val db= FirebaseFirestore.getInstance()
        holder.btnEnquiry.setOnClickListener {
//            val url = "https://api.whatsapp.com/send?phone=${marriageHall.number}"
//            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(url)
//            context.startActivity(i)
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 200)
                }
                else{
                    val intent1 = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", marriageHall.number, null))
                    context.startActivity(intent1)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            val tokenPath="Token/${marriageHall.currentUser}"
            val notificationPath="Notifications/${marriageHall.currentUser}/data/${marriageHall.documentId}"
            val notificationData= hashMapOf("renterId" to currentUser!!.uid, "renterNumber" to currentUser!!.phoneNumber,
                    "propertySubType" to marriageHall.hallName)
            db.document(tokenPath).get().addOnSuccessListener {
                if (it.exists()){
                    val token=it.get("token").toString()
                    FcmNotificationsSender(token,"A tenants enquiry you","hello i am interested in your ${marriageHall.hallName}",context,activity).SendNotifications()
                    db.document(notificationPath).set(notificationData)
                }
            }
        }

        val documentId=marriageHall.documentId
        val data= hashMapOf("path" to marriageHall.path)

        try {
            val favouritePath="Bookmarks/${currentUser!!.uid}/MarriageHall/$documentId"
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

        if(marriageHall.images!=null) {
//            val policy = StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build()
//            StrictMode.setThreadPolicy(policy)
            Picasso.get().load(marriageHall.images[0]).fit().error(R.drawable.about_us).into(holder.imgThumbnail)
        }

        holder.descreption.setOnClickListener {
            val student = FirebaseDatabase.getInstance().reference.child("History")
            val anotherChild = student.child(currentUser!!.uid)
            val anotherChild2=anotherChild.child(documentId.toString())
            val currentTime= Calendar.getInstance().time
            anotherChild2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        anotherChild2.child("path").setValue(marriageHall.path)
                        anotherChild2.child("address").setValue(marriageHall.fullAddress)
                        anotherChild2.child("monthlyRent").setValue(marriageHall.priceModel.toString())
                        anotherChild2.child("propertyType").setValue(marriageHall.propertyType)
                        anotherChild2.child("documentId").setValue(marriageHall.documentId)
                        anotherChild2.child("time").setValue(ServerValue.TIMESTAMP)
                    } else {
                        anotherChild2.child("time").setValue(ServerValue.TIMESTAMP)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            val intent= Intent(context, MarriageHallDetailActivity::class.java)
            intent.putExtra("name", marriageHall.ownerName)
            intent.putExtra("images", marriageHall.images)
            intent.putExtra("city", marriageHall.city)
            intent.putExtra("path", marriageHall.path)
            intent.putExtra("address", marriageHall.fullAddress)
            intent.putExtra("propertyType", marriageHall.propertyType)
            intent.putExtra("latitude", marriageHall.latitude)
            intent.putExtra("longitude", marriageHall.longitude)
            intent.putExtra("number", marriageHall.number)
            context.startActivity(intent)
            val tokenPath="Token/${marriageHall.currentUser}"
            val notificationPath="Notifications/${marriageHall.currentUser}/data/${marriageHall.documentId}"
            val notificationData= hashMapOf("renterId" to currentUser!!.uid, "renterNumber" to currentUser!!.phoneNumber,
                    "propertySubType" to marriageHall.hallName)
            db.document(tokenPath).get().addOnSuccessListener {
                if (it.exists()){
                    val token=it.get("token").toString()
                    FcmNotificationsSender(token,"A tenants enquiry you","hello i am interested in your ${marriageHall.hallName}",context,activity).SendNotifications()
                    db.document(notificationPath).set(notificationData)
                }
            }
        }

        holder.btnLocation.setOnClickListener {
            try {
                val mUriIntent= Uri.parse("geo:0,0?q=${marriageHall.latitude},${marriageHall.longitude}")
                val mMapIntent=Intent(Intent.ACTION_VIEW, mUriIntent)
                mMapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mMapIntent)
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

    override fun getItemCount(): Int {
        return marriageHallFilter.size
    }

    fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                marriageHallFilter = if (charSearch.isEmpty()) {
                    marriageHallItemList
                } else {
                    val resultList = ArrayList<MarriageHallListModel>()
                    for (row in marriageHallItemList) {
                        if (row.hallName!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.city!!.toString().toLowerCase(Locale.ROOT).contains(charSearch
                                        .toLowerCase(Locale.ROOT))
                                || row.path!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.fullAddress!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT)) || row.priceModel!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                        Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = marriageHallFilter
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                marriageHallFilter = results!!.values as java.util.ArrayList<MarriageHallListModel>
                notifyDataSetChanged()

            }

        }
    }

}