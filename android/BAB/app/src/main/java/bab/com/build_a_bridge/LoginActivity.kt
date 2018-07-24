package bab.com.build_a_bridge


import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import bab.com.build_a_bridge.enums.ExtraNames
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.UserType
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class LoginActivity : AppCompatActivity(),
        EmailVerificationFragment.EmailVerification,
        RegistrationFragment.UserTypeChoice {


    val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkFirebaseCredentials()

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
     * Checks if user is already signed in
     *
     * If no, start up FirebaseAuthUi login flow
     * If yes, check that if user accout is of type username/password that
     * they have verified there email
     */
    private fun checkFirebaseCredentials() {
        if (FirebaseAuth.getInstance().currentUser == null) runFirebaseAuthUi()
        else {
            val userData = FirebaseAuth.getInstance().currentUser!!.providerData
            for (i in 0 until userData.size)
                if (userData[i].providerId.equals("password"))
                    checkIfEmailVerified()

            // user from different provider ( facebook or google )
            checkIfNewUser()

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
                        .build(),
                RC_SIGN_IN)
    }

    /**
     * Since fragments are being swapped in and out, when back is pressed at any fragment within this
     * Activity execution backs out of application. This function checks where user and goes back
     * to appropriate fragment.
     */
    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentById(R.id.login_frame_container) is RegistrationUserInfoFragment){
            swapFragment(RegistrationFragment())
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
                        // User already exists
                        startActivity(Intent(applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    } else {
                        // User does not exist yet. Start RegistrationFragment
                        swapFragment(RegistrationFragment())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    // Do nothing
                }

            })
    }

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
     * Function called from EmailVerificationFragment when email has been verified
     */
    override fun emailVerified() {
        checkIfNewUser()
    }

    /**
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
            wasChosen -> bundle.putSerializable(ExtraNames.USER_TYPE.toString(), UserType.REFUGEE)
            else -> bundle.putSerializable(ExtraNames.USER_TYPE.toString(), UserType.VOLUNTEER)
        }

        fragment.arguments = bundle
        swapFragment(fragment)
    }

}
