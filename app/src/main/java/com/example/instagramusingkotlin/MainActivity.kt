package com.example.instagramusingkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.instagramusingkotlin.comm.SearchComm
import com.example.instagramusingkotlin.fragment.HomeFragment
import com.example.instagramusingkotlin.fragment.NotificationsFragment
import com.example.instagramusingkotlin.fragment.ProfileFragment
import com.example.instagramusingkotlin.fragment.SearchFragment
import com.example.instagramusingkotlin.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                var searchComm:SearchComm=ProfileFragment()
                searchComm.sendUser(user!!)
                moveToFragment(searchComm as ProfileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }
    var user: User ? = null
    var navView: BottomNavigationView ?  = null
    var index:Int= 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var intent=intent
        if (intent!=null){
            user= intent.getSerializableExtra("user")!! as User
        }
        else
            return

        navView = findViewById(R.id.nav_view)
        navView!!.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        moveToFragment(HomeFragment())
    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frame_container, fragment)
        fragmentTrans.commit()
    }

    override fun onResume() {
        super.onResume()
        if (R.id.nav_add_post==index){
            navView!!.selectedItemId=R.id.nav_home

        }
    }

    override fun onPause() {
        super.onPause()
        var menu:MenuItem=navView!!.getMenu().findItem(navView!!.getSelectedItemId())
        index=menu.itemId
    }
}
