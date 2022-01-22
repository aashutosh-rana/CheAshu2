package com.bcebhagalpur.cheashu.starter.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.activity.HomeActivity
import com.bcebhagalpur.cheashu.starter.adapter.WelcomeWalkthroughAdapter
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    var viewPager: ViewPager? = null
    var sliderDotspanel: LinearLayout? = null
    private var dotscount = 0
    private var dots: Array<ImageView?>? = null
    private var mAuth: FirebaseAuth? = null
    private var mobileNumber: String = ""
    private var verificationID: String = ""
    private var token: String = ""
    lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var etNumber: EditText
    private lateinit var btnSendOtp:Button
    private lateinit var btnResentOtp:Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNumber = findViewById(R.id.etNumber)
        mobileNumber = etNumber.text.toString()
        mAuth = FirebaseAuth.getInstance()
        btnSendOtp=findViewById(R.id.sendOtp)
        btnResentOtp=findViewById(R.id.resendOtp)
        progressBar=findViewById(R.id.progressBar)


        viewPager = findViewById<View>(R.id.viewPager) as ViewPager

        sliderDotspanel = findViewById<View>(R.id.SliderDots) as LinearLayout

        val viewPagerAdapter = WelcomeWalkthroughAdapter(this)

        viewPager!!.adapter = viewPagerAdapter
        dotscount = viewPagerAdapter.getCount()
        dots = arrayOfNulls<ImageView>(dotscount)

        for (i in 0 until dotscount) {
            dots!![i] = ImageView(this)
            dots!![i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_inactive_indicator))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            sliderDotspanel!!.addView(dots!![i], params)
        }

        dots!![0]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_active_indicator))

        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                for (i in 0 until dotscount) {
                    dots!![i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_inactive_indicator))
                }
                dots!![position]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.dot_active_indicator))
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        btnSendOtp.setOnClickListener {
            mobileNumber = etNumber.text.toString()

            if (mobileNumber.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                loginTask()
            } else {
                etNumber.error = "Enter valid phone number"
            }
        }

    }


    private fun loginTask() {

        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                if (p0 != null) {
                    signInWithPhoneAuthCredential(p0)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                        this@LoginActivity, "Invalid phone number or verification failed.",
                        Toast.LENGTH_LONG
                ).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                progressBar.visibility = View.GONE
                verificationID = p0
                token = p1.toString()

                etNumber.setText("")

                etNumber.hint = "Enter OTP"
                btnSendOtp.text = getString(R.string.v1)

                btnSendOtp.setOnClickListener {
                    try {
                        progressBar.visibility = View.VISIBLE
                        btnResentOtp.visibility=View.VISIBLE
                        try {
                            verifyAuthentication(verificationID, etNumber.text.toString())
                        }catch (e: IllegalArgumentException){
                            Toast.makeText(this@LoginActivity, "please enter otp", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }

                Log.e("Login : verificationId ", verificationID)
                Log.e("Login : token ", token)

                btnResentOtp.setOnClickListener {
                    if (mobileNumber != null) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                mobileNumber, 1, TimeUnit.MINUTES, this@LoginActivity, mCallBacks
                        )
                    } else {
                        Toast.makeText(
                                this@LoginActivity,
                                "please enter mobile number",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Time out", Toast.LENGTH_LONG).show()
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBacks
        )


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                    this@LoginActivity
            ) { task ->
                if (task.isSuccessful) {
                    task.result!!.user
                    progressBar.visibility = View.GONE
                    getToken(task.result.user!!.uid)
                    updateUi()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "invalid otp", Toast.LENGTH_LONG).show()
                    }
                }
            }


    }

    private fun verifyAuthentication(verificationID: String, otpText: String) {
        try {
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationID, otpText))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun updateUi() {
        mobileNumber = etNumber.text.toString()
        val intent1 = Intent(this@LoginActivity, HomeActivity::class.java)
        val intent2 = intent
        intent2.putExtra("mobileNumber", mobileNumber)
        startActivity(intent1)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
            })

        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    private fun getToken(userId: String) {
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token: String = Objects.requireNonNull(task.result)
                        val data= hashMapOf("token" to token)
                        FirebaseFirestore.getInstance().collection("Token").document(userId).set(data)
                    }
                }
    }
}