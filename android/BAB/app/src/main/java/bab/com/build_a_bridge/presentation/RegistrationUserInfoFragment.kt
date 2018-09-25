package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.RegistrationViewModel
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.enums.RegionCodes
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.FirebaseProfilePicUploadUtil
import bab.com.build_a_bridge.utils.ProfilePicUtil
import bab.com.build_a_bridge.utils.ValidationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_registration_user_info.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import java.io.IOException

/**
 * Gather information on the user and add them to the Firebase database
 */
class RegistrationUserInfoFragment : Fragment() {

    companion object {
        const val IMG_RESULT_CODE = 1
    }

    lateinit var filePath: Uri
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(RegistrationViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_user_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // disable button by default unitl fields are filled
        next_button.isEnabled = false
        checkForNames()
        spinnerSetup()
        setTextInputListeners()
        // Set click Listener for next button

        next_button.setOnClickListener { finishRegistration() }

        // Setup image View click Listener
        profile_pic_circle_image_view.setOnClickListener { choosePhoto() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)

            // save image to storage
            val imagePath = ProfilePicUtil.savePhoto(context!!, bitmap)
            val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
            prefs.putString(PreferenceNames.PROFILE_PICTURE.toString(), imagePath).apply()

            val photoUploader = FirebasePhotoUploader()
            photoUploader.uploadPhoto(filePath)
        }
    }

    /**
     * Check for names already obtained from firebase auth so user does not have to enter name twice
     */
    fun checkForNames() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val fullName = user.displayName
            val firstName = fullName?.split(" ")?.get(0)
            val lastName = fullName?.split(" ")?.get(1)

            first_name_text_input_edit_text.setText(firstName)
            last_name_text_input_edit_text.setText(lastName)

            viewModel.firstName = firstName
            viewModel.lastName = lastName
        }
    }

    /**
     * Text input listeners watch the edit text fields for changes and check that
     * input data is valid.
     */
    fun setTextInputListeners() {
        first_name_text_input_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                // If invalidChar is null then there is no invalid character and the name is valid
                val invalidChar = ValidationUtil.isNameValid(s.toString())
                if (invalidChar == null) {
                    viewModel.firstName = s.toString()
                    checkIfShouldEnableButton()

                } else {
                    first_name_text_input_edit_text.error = ("${getString(R.string.invalid_character)} '$invalidChar'")
                    next_button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }
        })

        last_name_text_input_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


                // If invalidChar is null then there is no invalid character and the name is valid
                val invalidChar = ValidationUtil.isNameValid(s.toString())
                if (invalidChar == null) {
                    viewModel.lastName = s.toString()
                    checkIfShouldEnableButton()
                } else {
                    last_name_text_input_edit_text.error = ("${getString(R.string.invalid_character)} '$invalidChar'")
                    next_button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }
        })

        phone_number_text_input_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.phoneNumber = s.toString()
                val isValid = ValidationUtil.isNumberValid(s.toString())
                if (isValid) {

                    checkIfShouldEnableButton()
                } else {
                    phone_number_text_input_edit_text.error = getString(R.string.phone_number_error)
                    next_button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }
        })
    }

    /**
     * Sets up the spinners for choosing state and region
     */
    private fun spinnerSetup() {
        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                states)
        state_spinner.adapter = adapter
        state_spinner.setTitle(getString(R.string.select_state))
        region_spinner.setTitle(getString(R.string.select_region))


        region_spinner.isEnabled = false
        state_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, selecetedItemView: View?, position: Int, p3: Long) {
                when (position) {
                    34 -> {
                        viewModel.state = RegionCodes.NEW_YORK
                        val regions = resources.getStringArray(R.array.new_york_regions)
                        val regionAdapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                                regions)
                        region_spinner.adapter = regionAdapter
                        region_spinner.isEnabled = true
                    }
                }
            }
        }

        region_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.region = when (viewModel.state) {
                    RegionCodes.NEW_YORK -> when (position) {
                        0 -> RegionCodes.ALBANY
                        1 -> RegionCodes.BUFFALO
                        2 -> RegionCodes.MT_VERNON
                        3 -> RegionCodes.NEW_YORK_CITY
                        4 -> RegionCodes.ROCHESTER
                        5 -> RegionCodes.SCHENECTADY
                        6 -> RegionCodes.SYRACUSE
                        7 -> RegionCodes.YONKERS
                        else -> null
                    }
                    else -> null
                }
                checkIfShouldEnableButton()
            }
        }
    }

    /**
     * Function checks to determine if the next button should be enabled.
     */
    fun checkIfShouldEnableButton() {
        next_button.isEnabled = (viewModel.firstName != null && first_name_text_input_edit_text.text.toString() != ""
                && viewModel.lastName != null && last_name_text_input_edit_text.text.toString() != ""
                && viewModel.state != null && viewModel.region != null)
    }

    /**
     * start up photo choosing intent
     */
    fun choosePhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), IMG_RESULT_CODE)
    }


    /**
     * Completed registration and pushes user information into the firebase database
     */
    fun finishRegistration() {
        // Validate user input from form
        when {
            (viewModel.state == null) -> activity?.toast(getString(R.string.please_select_state))
            (viewModel.region == null) -> activity?.toast(getString(R.string.please_select_region))
            else -> {

                // These values can not be null at this point of execution because the button is
                // only enabled when first, last, and phone number are not null
                // And state and region are checked above.
                val user = User(viewModel.firstName!!, viewModel.lastName!!,
                        viewModel.phoneNumber, viewModel.state!!, viewModel.region!!,
                         FirebaseAuth.getInstance().currentUser?.email!!,
                        FirebaseAuth.getInstance().uid.toString())



                val userDirectoryDb = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                        .child(FirebaseAuth.getInstance().uid!!)

                userDirectoryDb.setValue(user)

                // Save user to prefs
                val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
                prefs.putString(PreferenceNames.USER.toString(), Gson().toJson(user))
                prefs.apply()

                // Start main activity and clears stack of registration and login activities
                startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                activity?.finish()

            }
        }
    }

    inner class FirebasePhotoUploader : FirebaseProfilePicUploadUtil() {
        override fun onPhotoUploadSuccess() {
            snackbar(profile_pic_circle_image_view, getString(R.string.photo_upload_success))

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                // display on screen
                profile_pic_circle_image_view.setImageBitmap(bitmap)


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onPhotoUploadFailure() {
            snackbar(profile_pic_circle_image_view, getString(R.string.photo_upload_failure))
        }

        override fun photoUploadProgress(progress: Double) {
            // Do nothing, can use progress arg to update a progress bar if needed
        }
    }
}
