package com.bcebhagalpur.cheashu.starter.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MainActivity : AppCompatActivity() {

    val user=FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
         window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        Handler().postDelayed({
            if (user != null) {
                getToken()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }
    private fun getToken(){
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token: String = Objects.requireNonNull(task.result)
                        val data= hashMapOf("token" to token)
                        FirebaseFirestore.getInstance().collection("Token").document(user!!.uid).set(data)
                    }
                }
    }


}

