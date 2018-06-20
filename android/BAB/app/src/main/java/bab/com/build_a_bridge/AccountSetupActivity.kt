package bab.com.build_a_bridge

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.util.Log
import com.facebook.Profile
import kotlinx.android.synthetic.main.activity_account_setup.*

class AccountSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setup)


        val profile = Profile.getCurrentProfile()
        if(profile == null){
            Log.i(this.toString(), "NO PROFILE!!!")
        } else {
            first_name.setText(profile.firstName)
            last_name.setText(profile.lastName)
        }
    }
}
