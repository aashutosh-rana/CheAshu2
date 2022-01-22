package com.bcebhagalpur.cheashu.starter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bcebhagalpur.cheashu.R


class WelcomeWalkthroughAdapter(context: Context) : PagerAdapter() {
    private val context: Context
    private var layoutInflater: LayoutInflater? = null
    private val images = arrayOf<Int>(R.drawable.welcome_walkthrough, R.drawable.welcome_walkthrough, R.drawable.welcome_walkthrough)
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view: View = layoutInflater!!.inflate(R.layout.welcome_walkthrough_login, null)
        val imageView: ImageView = view.findViewById(R.id.imageView) as ImageView
        imageView.setImageResource(images[position])
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view: View = `object` as View
        vp.removeView(view)
    }

    init {
        this.context = context
    }
}