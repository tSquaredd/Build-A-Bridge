package bab.com.build_a_bridge


import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {

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
        when(item?.itemId){
            R.id.action_sign_out -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener { runFirebaseAuth() }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       when(requestCode){
           RC_SIGN_IN -> {
               // user signed in
               val response = IdpResponse.fromResultIntent(data)

               if(resultCode == Activity.RESULT_OK){
                   // Successfully signed in
                   val user = FirebaseAuth.getInstance().currentUser
                   if(user != null){
                       if(!user.isEmailVerified){
                           user.sendEmailVerification()
                                   .addOnCompleteListener { email_verification_message.visibility = View.VISIBLE }



                       }
                   }
               } else {
                   // Sign in failed. If response is null user cancelled sign in.
                   // Otherwise check response.getError().getErrorCode() to handle error
               }
           }
       }
    }

    fun checkFirebaseCredentials(){
        if(FirebaseAuth.getInstance().currentUser == null) runFirebaseAuth()

    }

    fun runFirebaseAuth(){
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



}
