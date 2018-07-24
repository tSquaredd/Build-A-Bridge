package bab.com.build_a_bridge

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

    var feedFragment: FeedFragment? = FeedFragment()
    var requestsFragment: RequestsFragment? = null
    var skillsFragment: SkillsFragment? = null
    var messagesFragment: MessagesFragment? = null
    var friendsFragment: FriendsFragment? = null
    var settingsFragment: SettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        setupNavigationListener()

        feedFragment = FeedFragment()
        swapFragments(feedFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setupNavigationListener(){
        nav_view.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer_layout.closeDrawers()

            when(it.itemId){
                R.id.nav_feed -> swapFragments(feedFragment)
                R.id.nav_requests -> {
                    if(requestsFragment == null) requestsFragment = RequestsFragment()
                    swapFragments(requestsFragment)
                }
                R.id.nav_skills -> {
                    if(skillsFragment == null) skillsFragment = SkillsFragment()
                    swapFragments(skillsFragment)
                }
                R.id.nav_messages -> {
                    if(messagesFragment == null) messagesFragment = MessagesFragment()
                    swapFragments(messagesFragment)
                }
                R.id.nav_friends -> {
                    if(friendsFragment == null) friendsFragment = FriendsFragment()
                    swapFragments(friendsFragment)
                }
                R.id.nav_settings -> {
                    if(settingsFragment == null) settingsFragment = SettingsFragment()
                    swapFragments(settingsFragment)
                }
                R.id.nav_sign_out -> {
                    ProfilePicUtil.removePhoto(applicationContext)
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }
            }

            true
        }
    }

    fun swapFragments(fragment: Fragment?){
        fragment?.let {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, it)
                    .addToBackStack(null)
                    .commit()
        }
    }

}
