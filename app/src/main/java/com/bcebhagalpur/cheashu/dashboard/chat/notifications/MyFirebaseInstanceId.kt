package com.bcebhagalpur.cheashu.dashboard.chat.notifications

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser= FirebaseAuth.getInstance().currentUser
//        val refreshToken=FirebaseInstanceId.getInstance().token
        val refreshToken1= FirebaseMessaging.getInstance().token
        if (firebaseUser!=null){
            updateToken(refreshToken1)
        }
    }

    private fun updateToken(refreshToken: Task<String>) {
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val ref= FirebaseDatabase.getInstance().reference.child("Tokens")
    }
}