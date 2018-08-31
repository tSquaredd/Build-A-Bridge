package bab.com.build_a_bridge.admin


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.BundleParamNames
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.ValidationUtil
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_admin_edit_skills.*
import bab.com.build_a_bridge.utils.FirebaseSkillIconUploadUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import java.io.File
import java.io.IOException

/**
 * This Fragment allows the user to edit the values and icon for any Skill selected from the
 * AdminSkillsFragment
 */
class AdminEditSkillsFragment : Fragment() {

    companion object {
        private const val IMG_RESULT_CODE = 78
    }

    lateinit var skill: Skill
    private var editingSkill = false
    var filePath: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_edit_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            editingSkill = true
            setupEditSkill()
        } else {
            admin_skills_delete_button.isEnabled = false
        }

        admin_skills_save_button.setOnClickListener { saveSkill() }

        admin_skills_delete_button.setOnClickListener { deleteSkill() }

        skills_icon.setOnClickListener { choosePhoto() }

        setupBottomActionBar()

    }

    /**
     * If the result is of IMG_RESULT_CODE then the result is the image for the icon.
     * Set the new image to the image View.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If image returned set to skills icon image bitmap
        if (requestCode == IMG_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                skills_icon.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Checks that the changes to skill are valid. Then uploads those changes to the Firebase database.
     *
     * Also checks that if a photo was uploaded that the photo is also uploaded to Firebase storage
     */
    private fun saveSkill() {
        val skillNameEmpty = admin_skill_name_edit_text.text.isNullOrEmpty()
        val skillDescriptionEmpty = admin_skill_description_edit_text.text.isNullOrEmpty()
        val skillNameCheck = ValidationUtil.isNameValid(admin_skill_name_edit_text.text.toString(), ' ')
        val skillDescriptionCheck = ValidationUtil.isNameValid(admin_skill_description_edit_text.text.toString(), ' ', '.', '\'', ',')


        when {
            // error checks
            (skillNameCheck != null) -> admin_skill_name_edit_text.error = "${getString(R.string.invalid_character)} '$skillNameCheck'"
            (skillDescriptionCheck != null) -> admin_skill_description_edit_text.error = "${getString(R.string.invalid_character)} '$skillDescriptionCheck'"
            (skillNameEmpty) -> admin_skill_name_edit_text.error = getString(R.string.must_specify_name)
            (skillDescriptionEmpty) -> admin_skill_description_edit_text.error = getString(R.string.must_add_description)
            // If no errors
            else -> {
                // Skill is valid add to skills list
                var db = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.SKILLS.toString())

                // If new skill get a new ID, else use same ID
                val skillId = if (editingSkill) skill.id else db.push().key.toString()


                db = db.child(skillId)

                skill = Skill(skillId,
                        admin_skill_name_edit_text.text.toString(),
                        admin_skill_description_edit_text.text.toString())

                db.setValue(skill)


                // upload skill icon if there is one.
                if (filePath != null) {


                    val photoUploader = FirebasePhotoUploader()
                    photoUploader.uploadPhoto(filePath!!, skill.id)
                } else activity?.onBackPressed()
            }
        }
    }


    private fun deleteSkill() {
        // Show dialog to get confirmation of deletion
        activity?.alert(getString(R.string.delete_skill_prompt), getString(R.string.delete_skill)) {
            positiveButton(getString(R.string.yes)) {
                // Delete the skill from db
                val db = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.SKILLS.toString())
                        .child(skill.id)

                db.removeValue()

                //remove skill image
                val imgDb = FirebaseStorage.getInstance().reference
                        .child(FirebaseStorageNames.SKILL_ICONS.toString())
                        .child(skill.id)

                imgDb.delete()


                activity?.onBackPressed()
            }
            neutralPressed(getString(R.string.no)) {
                // do nothing user cancelled deletion
            }
            iconResource = R.drawable.ic_warning
        }?.show()
    }

    /**
     * Function called when the fragment is supplied arguments.
     *
     * This means the fragment is being used to edit an already existing skill,
     * and details of the skill are being passed in.
     */
    private fun setupEditSkill() {
        val bundle = arguments
        bundle?.let {
            skill = it.getParcelable(BundleParamNames.SKILL.toString())

            // Loading image from firebase storage if there is one
            val storageRef = FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.SKILL_ICONS.toString())
                    .child(skill.id)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(storageRef)
                    .error(R.drawable.ic_default_skill)
                    .into(skills_icon)

            // set views from passed in data
            admin_skill_name_edit_text.setText(skill.name)
            admin_skill_description_edit_text.setText(skill.description)
        }
    }

    private fun choosePhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), IMG_RESULT_CODE)
    }

    /**
     * Extension of abstract class. Implemented abstract functions are called
     * on conditions. ( success, failure, progress )
     */
    inner class FirebasePhotoUploader : FirebaseSkillIconUploadUtil() {
        override fun onPhotUploadSuccess() {
            snackbar(skills_icon, getString(R.string.photo_upload_success))
            activity?.onBackPressed()

        }

        override fun onPhotoUploadFailure() {
            snackbar(skills_icon, getString(R.string.photo_upload_failure))
        }

        override fun photoUploadProgress(progress: Double) {
            // Do nothing, could use progress to update a progress bar if needed
        }

    }

    private fun setupBottomActionBar(){
        activity?.fab?.hide()
    }
}
