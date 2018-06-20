package bab.com.build_a_bridge

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.activity_login.*




lateinit var callbackManager: CallbackManager


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       facebookSetup()

        create_account_button.setOnClickListener {
            val intent = Intent(applicationContext, AccountSetupActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun facebookSetup(){

        callbackManager = CallbackManager.Factory.create()

        val loginButton = login_button as LoginButton

        // If you are using in a fragment, call loginButton.setFragment(this);

        loginButton.setReadPermissions("public_profile")

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                Log.i(this.toString(), "Faceboook login success")
                val intent = Intent(applicationContext, AccountSetupActivity::class.java)
                startActivity(intent)
            }

            override fun onCancel() {
                Log.i(this.toString(), "Facebook login cancelled")
            }

            override fun onError(exception: FacebookException) {
                Log.i(this.toString(), "Facebook login error")
            }
        })
    }
}
