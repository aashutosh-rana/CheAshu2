package com.bcebhagalpur.cheashu.dashboard.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bcebhagalpur.cheashu.R
import com.bumptech.glide.Glide

class FullImageActivity : AppCompatActivity() {

     private lateinit var myImage:ImageView
     private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        myImage = findViewById(R.id.image)
        back = findViewById(R.id.back)
        Glide.with(this)
            .load(intent.getStringExtra("image"))
            .placeholder(R.color.codeGray)
            .into(myImage)
        back.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0,0)
    }
}