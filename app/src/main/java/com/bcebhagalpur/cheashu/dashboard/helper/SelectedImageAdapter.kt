package com.bcebhagalpur.cheashu.dashboard.helper

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.FullImageActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target


class SelectedImageAdapter(context: Context, stringArrayList: ArrayList<String>) :
    RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    var context: Context
    var stringArrayList: ArrayList<String>
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.selected_image_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(stringArrayList[position])
            .placeholder(R.color.codeGray)
            .centerCrop()
            .into(holder.image)
        holder.image.setOnClickListener {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("image", stringArrayList[position])
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return stringArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.image) as ImageView
        }
    }

    init {
        this.context = context
        this.stringArrayList = stringArrayList
    }
}