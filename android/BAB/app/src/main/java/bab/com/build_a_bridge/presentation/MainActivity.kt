package bab.com.build_a_bridge.presentation

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import bab.com.build_a_bridge.*
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.admin.AdminSkillsFragment
import bab.com.build_a_bridge.enums.BundleParamNames
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainActivityViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Check if user signed in
        checkFirebaseCredentials(this)
    }

    /**
     * Called when Firebase Auth credentials have been verified
     */
    fun finishOnCreate() {
        setSupportActionBar(bottom_app_bar)
        title = ""


        val messageBundle = intent.extras.getBundle(BundleParamNames.MESSAGE_BUNDLE.toString())
        if (messageBundle != null) {
            swapFragments(ConversationsFragment(), false)
        } else if (intent.hasExtra("isMessage")) {
            swapFragments(ConversationsFragment(), false)

        } else {
            // Show feed fragment by default
            swapFragments(FeedFragment(), true)
        }

        fcmTokenCheckup()


        fab.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            when(currentFragment){
                is FeedFragment -> {
                    swapFragments(CreateRequestFragment(), true)
                }
                is FeedSkillFilterFragment -> {
                    val filterFragment = currentFragment as FeedSkillFilterFragment
                    if(filterFragment.skillAdapter.selectedSkillsList.isEmpty()){
                        toast(getString(R.string.empty_skills_filter))
                    } else {
                        viewModel.feedSkillFilterList = filterFragment.skillAdapter.selectedSkillsList
                        onBackPressed()
                    }
                }
                is RequestsFragment -> {
                    swapFragments(CreateRequestFragment(), true)
                }
                is AdminSkillsFragment -> {
                    swapFragments(AdminEditSkillsFragment(), true)
                }
                is CreateRequestFragment -> {
                    val fragment = currentFragment as CreateRequestFragment
                    if(fragment.isRequestReady())
                        fragment.requestCreation()
                    else {
                        // TODO: Give context as to why request is not ready
                    }

                }
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                openNavMenu()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openNavMenu() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
        val bottomFragment = BottomNavDrawerFragment()
        val bundle = Bundle()

        when (currentFragment) {
            is FeedFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.FEED)
            }
            is RequestsFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.REQUESTS)
            }
            is SkillsFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.SKILLS)
            }
            is ConversationsFragment, is MessagingFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.MESSAGES)
            }
            is FriendsFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.FRIENDS)
            }
            is SettingsFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.SETTINGS)
            }
            is AdminSkillsFragment, is AdminEditSkillsFragment -> {
                bundle.putString(BottomNavDrawerFragment.ARG_CURRENT_SELECTION, BottomNavDrawerFragment.ADMIN_SKILLS)
            }
        }

        bottomFragment.arguments = bundle
        bottomFragment.show(supportFragmentManager, bottomFragment.tag)


    }

    /**
     * puts a new fragment into View and has option of adding the fragment to the back stack
     */
    fun swapFragments(fragment: Fragment, addToBackStack: Boolean) {
        when (addToBackStack) {
            true -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit()
            }
            false -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .disallowAddToBackStack()
                        .commit()
            }
        }

//        bottomAppBarSetup()
    }

    /**
     * Check if user is signed in through FIrebaseAuth
     *
     * If not start up LoginActivity, if they are check that they are
     * a verified user existing in the DB
     */
    private fun checkFirebaseCredentials(context: Context) {
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
                    } else {
                        finishOnCreate()
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

    /**
     * Signs the user out of FirebaseAuth, removes profile picture and User from shared prefs
     */
    fun signOut() {
        ProfilePicUtil.removePhoto(applicationContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this).edit()
        prefs.remove(PreferenceNames.USER.toString())
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    /**
     * Check to see if FCM token has changed, and update DB if so
     */
    private fun fcmTokenCheckup() {

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener {
                    val token = it.result.token

                    if (token != viewModel.user.fcmToken) {
                        // Overwrite user data
                        viewModel.user.fcmToken = token

                        val prefEdit = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                        prefEdit.putString(PreferenceNames.USER.toString(), Gson().toJson(viewModel.user)).apply()


                        // TODO REMOVE
//                        // update user info on firebase
//                        FirebaseDatabase.getInstance().reference
//                                .child(FirebaseDbNames.USERS.toString())
//                                .child(FirebaseDbNames.STATE.toString())
//                                .child(viewModel.user.state.toString())
//                                .child(FirebaseDbNames.REGION.toString())
//                                .child(viewModel.user.region.toString())
//                                .child(viewModel.user.userId)
//                                .setValue(viewModel.user)

                        // Update user info in USER ID DIRECTORY
                        FirebaseDatabase.getInstance().reference
                                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                                .child(viewModel.user.userId)
                                .setValue(viewModel.user)
                    }
                }

    }
}
