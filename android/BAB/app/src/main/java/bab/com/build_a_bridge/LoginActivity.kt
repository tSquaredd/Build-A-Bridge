package bab.com.build_a_bridge


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bab.com.build_a_bridge.enums.*
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.facebook.login.Login
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.File
import java.util.*


class LoginActivity : AppCompatActivity(),
        EmailVerificationFragment.EmailVerification,
        RegistrationFragment.UserTypeChoice {

    companion object {
        const val RC_SIGN_IN = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (FirebaseAuth.getInstance().currentUser == null)
            runFirebaseAuthUi()
        else
            checkIfEmailVerified()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sign_out -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            ProfilePicUtil.removePhoto(applicationContext)
                            runFirebaseAuthUi()
                        }
            }
        }
        return true
    }

    /**
     * activity result in this case comes from the FirebaseAuth UI Activity.
     *
     * Checks whether user was signed in, and
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                // user signed in
                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in

                    val providerCode = response?.providerType
                    /*
                     * PROVIDERS:
                     *
                     * Google: google.com
                     * Facebook: facebook.com
                     * Email / Password: password
                     */

                    // Now if user is from the password provider, check for email verification
                    // If from another provider go directly to checkIfNewUser()

                    if (providerCode.equals("password")) {
                        checkIfEmailVerified()
                    } else {
                        checkIfNewUser()
                    }


                } else {
                    // Sign in failed. If response is null user cancelled sign in.
                    // Otherwise check response.getError().getErrorCode() to handle error
                }
            }
        }
    }


    /**
     * Start up Firebase Auth UI Activity which allows users to sign in via
     * email / password
     * google
     * facebook
     */
    private fun runFirebaseAuthUi() {
        // Choose authentication providers
        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.bab_logo)
                        .setTheme(R.style.BabBackground)
                        .build(),
                RC_SIGN_IN)
    }

    /**
     * Since fragments are being swapped in and out, when back is pressed at any fragment within this
     * Activity execution backs out of application. This function checks where user and goes back
     * to appropriate fragment.
     */
    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.login_frame_container) is RegistrationUserInfoFragment) {
            signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        } else {
            super.onBackPressed()
        }
    }


    /**
     * Swaps fragment in frame container of activity_login.xml
     */
    fun swapFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.login_frame_container, fragment)
                .commit()
    }

    /**
     * Checks if the current user exists within the database. If they do forward execution along to
     * to the HomeActivity
     *
     * If they do not exists in DB send them to the RegistrationFragment
     */
    private fun checkIfNewUser() {

        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(FirebaseAuth.getInstance().uid!!)

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    // turn on progress bar
                    login_progress_bar.visibility = View.VISIBLE
                    // User already exists

                    // get user data for prefs
                    val user = dataSnapshot.getValue(User::class.java)
                    val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                    prefs.putString(PreferenceNames.USER.toString(), Gson().toJson(user)).apply()

                    // Check for profile picture
                    val storageRef = FirebaseStorage.getInstance().reference
                            .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                            .child(FirebaseAuth.getInstance().uid!!)

                    val contextWrapper = ContextWrapper(applicationContext)
                    val fileDirectory = contextWrapper.getDir(ProfilePicUtil.PHOTO_DIRECTORY, Context.MODE_PRIVATE)
                    val filePath = File(fileDirectory, ProfilePicUtil.FILE_NAME)

                    storageRef.getFile(filePath).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(filePath.toString())
                        val savedFilePath = ProfilePicUtil.savePhoto(applicationContext, bitmap)
                        prefs.putString(PreferenceNames.PROFILE_PICTURE.toString(), savedFilePath).apply()
                        startActivity(Intent(applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }.addOnFailureListener {
                        startActivity(Intent(applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }


                } else {
                    // User does not exist yet. Start RegistrationUserInfoFragment
                    // TODO: If we need to track user type ( ref / vol ) change this to RegistrationFragment()
                    swapFragment(RegistrationUserInfoFragment())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

        })
    }

    /**
     * If user email is not verified starts the EmailVerificationFragment
     * Otherwise sends execution to checkIfNewUser
     */
    private fun checkIfEmailVerified() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.reload()
            if (!user.isEmailVerified) {
                user.sendEmailVerification()
                        .addOnCompleteListener {
                            swapFragment(EmailVerificationFragment())
                        }
            } else {
                checkIfNewUser()
            }
        }
    }

    /**
     * Sign the user out and start the LoginActivity over again
     */
    private fun signOut() {
        ProfilePicUtil.removePhoto(applicationContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this).edit()
        prefs.remove(PreferenceNames.USER.toString())
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    /**
     * Function called from EmailVerificationFragment when email has been verified
     */
    override fun emailVerified() {
        checkIfNewUser()
    }

    /**
     *
     * NOT BEING USED AT THE MOMENT
     * Called from RegistrationFragment.
     *
     * When called this user has selected either Refugee / Immigrant or
     * Volunteer from RegistrationFragment
     *
     *
     */
    override fun refugee(wasChosen: Boolean) {
        val fragment = RegistrationUserInfoFragment()
        val bundle = Bundle()
        when {
            wasChosen -> bundle.putSerializable(BundleParamNames.USER_TYPE.toString(), UserType.REFUGEE)
            else -> bundle.putSerializable(BundleParamNames.USER_TYPE.toString(), UserType.VOLUNTEER)
        }

        fragment.arguments = bundle
        swapFragment(fragment)
    }

}
