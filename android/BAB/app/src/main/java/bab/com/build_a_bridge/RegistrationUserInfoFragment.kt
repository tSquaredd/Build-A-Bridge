package bab.com.build_a_bridge


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.utils.FirebaseProfilePicUploadUtil
import bab.com.build_a_bridge.utils.ProfilePicUtil
import kotlinx.android.synthetic.main.fragment_registration_user_info.*
import org.jetbrains.anko.design.snackbar
import java.io.IOException


class RegistrationUserInfoFragment : Fragment() {

    private val IMG_RESULT_CODE = 1
    lateinit var filePath: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                states)
        state_spinner.adapter = adapter
        state_spinner.setTitle(getString(R.string.select_state))


        region_spinner.isEnabled = false

        // Set state spinner adapter so that it populates regions based on selection
        state_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, selecetedItemView: View?, position: Int, p3: Long) {
                when(position){
                    34 -> {
                        val regions = resources.getStringArray(R.array.new_york_regions)
                        val regionAdapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                               regions )
                        region_spinner.adapter = regionAdapter
                        region_spinner.isEnabled = true
                    }
                }
            }
        }


        // Setup image view click listener
        profile_pic_circle_image_view.setOnClickListener { choosePhoto() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMG_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data

            val photoUploader = FirebasePhotoUploader()
            photoUploader.uploadPhoto(filePath, context!!)

        }
    }

    fun choosePhoto(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), IMG_RESULT_CODE)



    }

    inner class FirebasePhotoUploader : FirebaseProfilePicUploadUtil() {
        override fun onPhotoUploadSuccess() {
            snackbar(profile_pic_circle_image_view, getString(R.string.photo_upload_success))

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                // display on screen
                profile_pic_circle_image_view.setImageBitmap(bitmap)


                // save image to storage
                val imagePath = ProfilePicUtil.savePhoto(context!!, bitmap)
                val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
                prefs.putString(PreferenceNames.PROFILE_PICTURE.toString(), imagePath).apply()


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
