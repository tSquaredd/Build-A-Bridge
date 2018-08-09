package bab.com.build_a_bridge


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_email_verification.*

/**
 * When a User signs up with email / password they are required to verify the email address.
 * This fragment directs user to check email for confirmation email and enables User to
 * send another verification email or check for authorization status.
 */

class EmailVerificationFragment : Fragment() {
    private lateinit var callback: EmailVerification

    interface EmailVerification {
        fun emailVerified()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        send_verification_email_button.setOnClickListener {
            val user = getUser()
            user?.sendEmailVerification()
        }

        check_verification_button.setOnClickListener {
            if (checkVerificationStatus()) callback.emailVerified()
            else {
                // User not verified
                showContent()
                Toast.makeText(context, R.string.email_not_verified_toast, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            callback = activity as EmailVerification
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement EmailVerification interface")
        }
    }

    override fun onResume() {
        super.onResume()
        val user = getUser()
        if (checkVerificationStatus()) callback.emailVerified()
        else showContent()
    }

    private fun checkVerificationStatus(): Boolean {
        showLoading()
        val user = getUser()
        if (user != null) {
            user.reload()
            return user.isEmailVerified
        }
        return false

    }

    private fun getUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun showLoading() {
        content_layout.visibility = View.INVISIBLE
        loading_layout.visibility = View.VISIBLE
    }

    private fun showContent() {
        loading_layout.visibility = View.INVISIBLE
        content_layout.visibility = View.VISIBLE
    }


}
