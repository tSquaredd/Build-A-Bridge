package bab.com.build_a_bridge.admin


import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.BundleParamNames
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.ValidationUtil
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_admin_edit_skills.*
import java.util.*
import android.widget.LinearLayout
import bab.com.build_a_bridge.utils.FirebaseSkillIconUploadUtil
import org.jetbrains.anko.design.snackbar
import java.io.IOException


class AdminEditSkillsFragment : Fragment() {

    val IMG_RESULT_CODE = 78
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    var editingSkill = false
    lateinit var skillName: String
    lateinit var skillDescription: String
    lateinit var skillId: String
    var skillListIndex = 0
    var filePath: Uri? = null
    lateinit var skill: Skill

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

        admin_edit_skills_icon.setOnClickListener { choosePhoto() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == IMG_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
             filePath = data.data

            try{
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                admin_edit_skills_icon.setImageBitmap(bitmap)
            } catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    fun saveSkill() {
        val skillNameEmpty = admin_skill_name_edit_text.text.isNullOrEmpty()
        val skillDescriptionEmpty = admin_skill_description_edit_text.text.isNullOrEmpty()
        val skillNameCheck = ValidationUtil.isNameValid(admin_skill_name_edit_text.text.toString(), ' ')
        val skillDescriptionCheck = ValidationUtil.isNameValid(admin_skill_description_edit_text.text.toString(), ' ')



        when {
            (skillNameCheck != null) -> admin_skill_name_edit_text.error = "${getString(R.string.invalid_character)} '$skillNameCheck'"
            (skillDescriptionCheck != null) -> admin_skill_description_edit_text.error = "${getString(R.string.invalid_character)} '$skillDescriptionCheck'"
            (skillNameEmpty) -> admin_skill_name_edit_text.error = getString(R.string.must_specify_name)
            (skillDescriptionEmpty) -> admin_skill_description_edit_text.error = getString(R.string.must_add_description)
            else -> {
                // Skill is valid add to skills list
                skill = Skill(if (editingSkill) skillId else viewModel.systemSkillsList.size.toString(),
                        admin_skill_name_edit_text.text.toString(),
                        admin_skill_description_edit_text.text.toString())

                val db = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.SKILLS.toString())
                        .child(skill.skillId)

                db.setValue(skill)



                // upload skill icon if there is one.
                if(filePath != null){
                    val photoUploader = FirebasePhotoUploader()
                    photoUploader.uploadPhoto(filePath!!, skillId)
                } else {
                    if (editingSkill) viewModel.systemSkillsList.removeAt(skillListIndex)
                    viewModel.systemSkillsList.add(skill)
                    notifyAdapterDatasetChanged()
                    activity?.onBackPressed()
                }

            }
        }
    }

    fun deleteSkill() {

        // TODO: A lot more will need to be done within database when a skill is removed
        val builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                else AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.delete_skill))
        .setMessage(getString(R.string.delete_skill_prompt))
        .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                // Delete the skill
                val db = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.SKILLS.toString())
                        .child(skillId)

                db.removeValue()

                viewModel.systemSkillsList.removeAt(skillListIndex)
                notifyAdapterDatasetChanged()
                activity?.onBackPressed()
            }

        })
        .setNeutralButton(getString(R.string.no), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                // Do nothing, user cancelled deletion
            }

        })
                .setIcon(R.drawable.ic_warning)
                .show()
    }

    fun setupEditSkill() {
        val bundle = arguments
        bundle?.let {
            skillName = it.getString(BundleParamNames.SKILL_NAME.toString())
            skillDescription = it.getString(BundleParamNames.SKILL_DESCRIPTION.toString())
            skillId = it.getString(BundleParamNames.SKILL_ID.toString())
            skillListIndex = it.getInt(BundleParamNames.SKILL_LIST_INDEX.toString())

            admin_skill_name_edit_text.setText(skillName)
            admin_skill_description_edit_text.setText(skillDescription)
        }
    }

    fun notifyAdapterDatasetChanged() {
        val mainActivity = activity as MainActivity
        val parentFragment = mainActivity.adminSkillsFragment
        parentFragment?.skillAdapter?.notifyDataSetChanged()
    }

    private fun choosePhoto(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), IMG_RESULT_CODE)
    }

    inner class FirebasePhotoUploader : FirebaseSkillIconUploadUtil() {
        override fun onPhotUploadSuccess() {
            snackbar(admin_edit_skills_icon, getString(R.string.photo_upload_success))
            if (editingSkill) viewModel.systemSkillsList.removeAt(skillListIndex)
            viewModel.systemSkillsList.add(skill)
            notifyAdapterDatasetChanged()
            activity?.onBackPressed()

        }

        override fun onPhotoUploadFailure() {
           snackbar(admin_edit_skills_icon, getString(R.string.photo_upload_failure))
        }

        override fun photoUploadProgress(progress: Double) {
            // Do nothing, could use progress to update a progress bar if needed
        }

    }
}
