package com.bcebhagalpur.cheashu.drawer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FeedbackActivity : AppCompatActivity() {

    private lateinit var etF1: EditText
    private lateinit var etF2:EditText
    private lateinit var etF3:EditText
    private lateinit var etF4:EditText
    private lateinit var etF5:EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var txtRating: TextView
    private lateinit var btnSendFeedback: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        etF1=findViewById(R.id.etF1)
        etF2=findViewById(R.id.etF2)
        etF3=findViewById(R.id.etF3)
        etF4=findViewById(R.id.etF4)
        etF5=findViewById(R.id.etF5)
        ratingBar=findViewById(R.id.ratingBar)
        txtRating=findViewById(R.id.txtRate)
        btnSendFeedback=findViewById(R.id.btnSendFeedback)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            txtRating.text = rating.toString()
        }
        btnSendFeedback.setOnClickListener {
            try {
                val fa=etF1.text.toString().trim()
                val fb=etF2.text.toString().trim()
                val fc=etF3.text.toString().trim()
                val fd=etF4.text.toString().trim()
                val fe=etF5.text.toString().trim()
                val ratingValue= ratingBar.rating.toString()
                val array= arrayOf(fa,fb,fc,fd,fe)
                val array1= arrayOf("search","crash","realContent","alreadyBooked","otherIssue")
                val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
                val number= FirebaseAuth.getInstance().currentUser!!.phoneNumber
                val mDatabase= FirebaseDatabase.getInstance().getReference("Feedback")
                val currentUserDb=mDatabase.child(currentUser)

                currentUserDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in 0..4){
                            if (!TextUtils.isEmpty(array[i])){
                                currentUserDb.child(array1[i]).setValue(array[i])
                                currentUserDb.child("rating").setValue(ratingValue)
                                currentUserDb.child("number").setValue(number!!.toString())
                            }
                        }
                     }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
                Toast.makeText(this,"Thank you so much to give us your time",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

}
