package com.bcebhagalpur.cheashu.dashboard.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bcebhagalpur.cheashu.BuildConfig
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.fragment.*
import com.bcebhagalpur.cheashu.drawer.activity.BookmarkActivity
import com.bcebhagalpur.cheashu.drawer.activity.HistoryActivity
import com.bcebhagalpur.cheashu.drawer.activity.MyUploadsActivity
import com.bcebhagalpur.cheashu.drawer.activity.FeedbackActivity
import com.bcebhagalpur.cheashu.starter.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var contentView: LinearLayout
//    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var filterFragment: FilterFragment
    private lateinit var notificationFragment: NotificationFragment
    private var previousMenuItem: MenuItem? = null
    private var clicked=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_home)
        changeColor()
        val intent= Intent()
        val extras=intent.getStringExtra("NotificationKey")
        if (extras!=null && extras == "Notification"){
            supportFragmentManager.beginTransaction().replace(
                    R.id.frameLayout,
                    NotificationFragment())
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
//        menuIcon = findViewById(R.id.menu_icon)
        contentView = findViewById(R.id.content)
//        toolbar=findViewById(R.id.toolBar)
//        setUpToolbar()

//        navigationDrawer()
        drawerHeaderItemHandle()
        bottomNavigationView=findViewById(R.id.bottomNavigationView)
        exploreFragment()
        bottom()

        exploreFragment()
    }

    private fun bottom(){
        bottomNavigationView.setOnNavigationItemSelectedListener {

            if (previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.explore -> {
                    exploreFragment()
//                    supportActionBar!!.title = "Explore Homes"
                }
                R.id.filter -> {
                    filterFragment = FilterFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, filterFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
//                    supportActionBar?.hide()
                }

                R.id.notification -> {
                    notificationFragment = NotificationFragment()
                    supportFragmentManager.beginTransaction().replace(
                            R.id.frameLayout,
                            notificationFragment
                    )
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
//                    supportActionBar?.hide()
                }
                R.id.upload -> {
                    val bottomSheet=com.bcebhagalpur.cheashu.dashboard.helper.BottomSheetDialog()
                    bottomSheet.show(supportFragmentManager,"ModalBottomSheet")
                    clicked=!clicked
                }
                R.id.more->{
                    navigationView.bringToFront()
                    navigationView.setNavigationItemSelectedListener(this)
                        if (drawerLayout.isDrawerVisible(GravityCompat.END)) drawerLayout.closeDrawer(
                                GravityCompat.END
                        ) else drawerLayout.openDrawer(GravityCompat.END)
                    animateNavigationDrawer()
                }
            }
            true
        }
    }



    @SuppressLint("ResourceAsColor")
    private fun exploreFragment(){
        exploreFragment= ExploreFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, exploreFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
//        supportActionBar?.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id!= android.R.id.home){
            exploreFragment()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return true
    }

    private fun animateNavigationDrawer() {
        val endScale = 0.7f
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val diffScaledOffset: Float = slideOffset * (1 - endScale)
                val offsetScale = 1 - diffScaledOffset
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = contentView.width * diffScaledOffset / 2
                val xTranslation = xOffsetDiff - xOffset
                contentView.translationX = xTranslation
            }
        })
    }

    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)

        }else{
            when(supportFragmentManager.findFragmentById(R.id.frameLayout)){
                !is ExploreFragment -> {
                    exploreFragment()
                }
                else-> {
                    val a = Intent(Intent.ACTION_MAIN)
                    a.addCategory(Intent.CATEGORY_HOME)
                    a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(a)
                }
            }
        }
    }

    private fun drawerHeaderItemHandle(){
        val headerView=navigationView.getHeaderView(0)
        val rl1= headerView.findViewById<RelativeLayout>(R.id.rl_1)
        val rl2=headerView.findViewById<RelativeLayout>(R.id.rl_two)
        val rl3=headerView.findViewById<RelativeLayout>(R.id.rl_three)
        val rl4=headerView.findViewById<RelativeLayout>(R.id.rl_four)
        val rl5=headerView.findViewById<RelativeLayout>(R.id.rl_five)
        val rl6=headerView.findViewById<RelativeLayout>(R.id.rl_6)
        val rl7=headerView.findViewById<RelativeLayout>(R.id.rl_7)
        val rl8=headerView.findViewById<RelativeLayout>(R.id.rl_8)
        val rl9=headerView.findViewById<RelativeLayout>(R.id.rl_9)
        val rl10=headerView.findViewById<RelativeLayout>(R.id.rl_10)
        val logOut=headerView.findViewById<TextView>(R.id.log_out)
        val userId= FirebaseAuth.getInstance().currentUser

        logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
//        rl.setOnClickListener {
//
//            startActivity(Intent(this, StudentProfileActivity::class.java))
//        }
        rl2.setOnClickListener {
            startActivity(Intent(this,MyUploadsActivity::class.java))
        }
        rl3.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        rl4.setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))
        }
        rl5.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CheAshu the complete room rental solution")
                var shareMessage =
                    "\nLet me recommend you CheAshu which help you to find the rooms on rent\n\n"
                shareMessage =
                    """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            drawerLayout.closeDrawers()
        }

                rl6.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        rl8.setOnClickListener {
            try{
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            }catch (e: ActivityNotFoundException){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
            drawerLayout.closeDrawers()
        }
//        logOut.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
    }


    private fun changeColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.white)
        }
    }
//    private fun setUpToolbar() {
//        setSupportActionBar(toolbar)
//        supportActionBar!!.title="Explore Homes"
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//    }


}
