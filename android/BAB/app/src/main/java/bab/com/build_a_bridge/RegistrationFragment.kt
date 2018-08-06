package bab.com.build_a_bridge


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_registration.*

/**
 *
 * THIS FRAGMENT IS NOT BEING USED CURRENTLY
 *
 * IF WE NEED TO ADD THIS DATA TO USER ACCOUNT WE CAN STILL USE THIS
 *
 * JUST ALTER CODE AT THE TO DO IN LoginActivity
 * This fragment is the begining of registration and prompts the user to choose whether they are
 * of type Refugee/Immigrant or Volunteer
 */
class RegistrationFragment : Fragment() {

    private lateinit var activityCallback: UserTypeChoice

    interface UserTypeChoice{
        fun refugee(wasChosen: Boolean)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refugee_immigrant_choice_button.setOnClickListener {
            activityCallback.refugee(wasChosen = true)
        }

        volunteer_choice_button.setOnClickListener {
            activityCallback.refugee(wasChosen = false)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try{
            activityCallback = activity as UserTypeChoice
        } catch ( e: ClassCastException){
            throw ClassCastException("${activity.toString()} must implement UserTypeChoice Interface")
        }
    }

}
