package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.utils.*
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_registration_user_info.*
import org.jetbrains.anko.design.snackbar
import java.io.IOException


class EditProfileFragment : Fragment() {

    lateinit var filePath: Uri

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    var isFabVisible = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel.getRegions()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomActionBar()

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(FirebaseStorageRefUtil.profilePicRef(viewModel.user.userId))
                .into(profile_pic_circle_image_view)

        first_name_text_input_edit_text.setText(viewModel.user.firstName, TextView.BufferType.EDITABLE)
        last_name_text_input_edit_text.setText(viewModel.user.lastName, TextView.BufferType.EDITABLE)
        phone_number_text_input_edit_text.setText(viewModel.user.phoneNumber, TextView.BufferType.EDITABLE)
        next_button.visibility = View.GONE

        spinnerSetup()
        setTextInputListeners() // Setup image View click Listener
        profile_pic_circle_image_view.setOnClickListener { choosePhoto() }

    }

    private fun spinnerSetup() {
        val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                viewModel.states)
        state_spinner.adapter = adapter

        region_spinner.isEnabled = false
        state_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, selecetedItemView: View?, position: Int, p3: Long) {
                viewModel.user.state = viewModel.states[position]

                val regions = viewModel.areas[viewModel.user.state]

                val regionAdapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                        regions)

                region_spinner.adapter = regionAdapter
                region_spinner.isEnabled = true
            }
        }

        region_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.areas[viewModel.user.state]?.let {
                    viewModel.user.region = it[position]
                }


            }
        }
    }

    fun setTextInputListeners() {
        first_name_text_input_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                // If invalidChar is null then there is no invalid character and the name is valid
                val invalidChar = ValidationUtil.isNameValid(s.toString())
                if (invalidChar == null) {
                    viewModel.user.firstName = s.toString()
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
                    viewModel.user.lastName = s.toString()
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
                viewModel.user.phoneNumber = s.toString()
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

    private fun setupBottomActionBar() {
        if(activity?.fab?.isOrWillBeHidden!!){
            activity?.fab?.show()
        } else {
            activity?.fab?.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    activity?.bottom_app_bar?.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    activity?.fab?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_done))
                    val handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            activity?.fab?.show()
                        }

                    }, 200)
                }
            })
        }
    }

    fun saveProfile() {
        Log.i("EditProfileFragment", "saveProfile: Saving Profile data")
        Log.i("EditProfileFragment", "saveProfile: User is ${viewModel.user.toString()}")
        val dbRef = FirebaseDbRefUtil.getUserRef(viewModel.user.userId)
        dbRef.setValue(viewModel.user)

        // Save user to prefs
        val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
        prefs.putString(PreferenceNames.USER.toString(), Gson().toJson(viewModel.user))
        prefs.apply()
    }

    fun checkIfShouldEnableButton() {
        val shouldEnable = (first_name_text_input_edit_text.text.toString() != ""
                && last_name_text_input_edit_text.text.toString() != ""
                && viewModel.user.state != null && viewModel.user.region != null)

        if(!shouldEnable && isFabVisible) {
            activity?.fab?.hide()
            isFabVisible = false
        }
        else if(shouldEnable && !isFabVisible) {
            setupBottomActionBar()
            isFabVisible = true
        }
    }

    /**
     * start up photo choosing intent
     */
    fun choosePhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), RegistrationUserInfoFragment.IMG_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RegistrationUserInfoFragment.IMG_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
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

            Glide.get(context).clearMemory()
            Thread{
                Glide.get(context).clearDiskCache()
            }.start()

        }

        override fun onPhotoUploadFailure() {
            snackbar(profile_pic_circle_image_view, getString(R.string.photo_upload_failure))
        }

        override fun photoUploadProgress(progress: Double) {
            // Do nothing, can use progress arg to update a progress bar if needed
        }
    }
}
