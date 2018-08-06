package bab.com.build_a_bridge

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.admin.AdminSkillsFragment
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFirebaseCredentials(this)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        setupNavigationListener()
        setNavigationHeader()


        // Show feed fragment by default
        swapFragments(FeedFragment(), true)
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

    private fun setupNavigationListener() {
        nav_view.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer_layout.closeDrawers()

            when (it.itemId) {
                R.id.nav_feed -> swapFragments(FeedFragment(), true)
                R.id.nav_requests -> swapFragments(RequestsFragment(), true)
                R.id.nav_skills -> swapFragments(SkillsFragment(), true)
                R.id.nav_messages -> swapFragments(MessagesFragment(), true)
                R.id.nav_friends -> swapFragments(FriendsFragment(), true)
                R.id.nav_settings -> swapFragments(SettingsFragment(), true)
                R.id.nav_admin_skills -> swapFragments(AdminSkillsFragment(), true)
                R.id.nav_sign_out -> {
                    signOut()
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


    fun swapFragments(fragment: Fragment?, addToBackStack: Boolean) {
        when (addToBackStack) {
            true -> {
                fragment?.let {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, it)
                            .addToBackStack(null)
                            .commit()
                }
            }
            false -> {
                fragment?.let {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, it)
                            .disallowAddToBackStack()
                            .commit()

                }
            }
        }
    }

        fun checkFirebaseCredentials(context: Context) {
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(context, LoginActivity::class.java))
                finish()
            } else {
                val userDbRef = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                userDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Do nothing
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            startActivity(Intent(context, LoginActivity::class.java))
                            finish()
                        }
                    }

                })
                val userData = FirebaseAuth.getInstance().currentUser!!.providerData
                for (i in 0 until userData.size)
                    if (userData[i].providerId.equals("password"))
                        checkIfEmailVerified(context)
            }
        }

        private fun checkIfEmailVerified(context: Context) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                user.reload()
                if (!user.isEmailVerified) {
                    startActivity(Intent(context, LoginActivity::class.java))
                }
            }
        }

        private fun signOut() {
            ProfilePicUtil.removePhoto(applicationContext)
            val prefs = PreferenceManager.getDefaultSharedPreferences(this).edit()
            prefs.remove(PreferenceNames.USER.toString())
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }

    }
