package bab.com.build_a_bridge

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.admin.AdminSkillsFragment
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }

    var feedFragment: FeedFragment? = FeedFragment()
    var requestsFragment: RequestsFragment? = null
    var skillsFragment: SkillsFragment? = null
    var messagesFragment: MessagesFragment? = null
    var friendsFragment: FriendsFragment? = null
    var settingsFragment: SettingsFragment? = null
    var adminSkillsFragment: AdminSkillsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }


//        viewModel.skillLiveDataList.observe(this, Observer { skillList: List<Skill>? ->
//            skillList?.let { for(skill in skillList) getIcon(skill) }
//        })
        setupNavigationListener()
        setNavigationHeader()


        // Show feed fragment by default
        feedFragment = FeedFragment()
        swapFragments(feedFragment)
        // show feed fragment selected in nav drawer
        nav_view.menu.getItem(0).isChecked = true
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


//    private fun getIcon(skill: Skill){
//        val storageRef = FirebaseStorage.getInstance().reference
//                .child(FirebaseStorageNames.SKILL_ICONS.toString())
//                .child(skill.id)
//
//
//    }
    private fun setupNavigationListener() {
        nav_view.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer_layout.closeDrawers()

            when (it.itemId) {
                R.id.nav_feed -> swapFragments(feedFragment)
                R.id.nav_requests -> {
                    if (requestsFragment == null) requestsFragment = RequestsFragment()
                    swapFragments(requestsFragment)
                }
                R.id.nav_skills -> {
                    if (skillsFragment == null) skillsFragment = SkillsFragment()
                    swapFragments(skillsFragment)
                }
                R.id.nav_messages -> {
                    if (messagesFragment == null) messagesFragment = MessagesFragment()
                    swapFragments(messagesFragment)
                }
                R.id.nav_friends -> {
                    if (friendsFragment == null) friendsFragment = FriendsFragment()
                    swapFragments(friendsFragment)
                }
                R.id.nav_settings -> {
                    if (settingsFragment == null) settingsFragment = SettingsFragment()
                    swapFragments(settingsFragment)
                }
                R.id.nav_admin_skills -> {
                    if (adminSkillsFragment == null) adminSkillsFragment = AdminSkillsFragment()
                    swapFragments(adminSkillsFragment)
                }
                R.id.nav_sign_out -> {
                    ProfilePicUtil.removePhoto(applicationContext)
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this).edit()
                    prefs.remove(PreferenceNames.USER.toString())
                    prefs.apply()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }
            }

            true
        }
    }

    private fun setNavigationHeader() {
        val header = nav_view.getHeaderView(0)
        val profilePic = ProfilePicUtil.loadPhotoFromInternalStorage(applicationContext)
        if (profilePic != null) {
            header.nav_profile_image_view.setImageBitmap(profilePic)
        }

        header.nav_user_name_text_view.text = viewModel.user?.firstName

    }

    fun swapFragments(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, it)
                    .addToBackStack(null)
                    .commit()
        }
    }

}
