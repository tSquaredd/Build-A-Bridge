package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Skill
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.skill_toggle_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Adapter for Skill objects with a toggleSwitch for enabling or disabling a certain Skill
 * from a User skill set within the DB
 */
class SkillToggleAdapter(private val userSkills: ArrayList<String>) :
        RecyclerView.Adapter<SkillToggleAdapter.SkillToggleHolder>() {

    var skillList: List<Skill> = listOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SkillToggleHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.skill_toggle_item, parent, false)
        return SkillToggleHolder(view)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: SkillToggleHolder, position: Int) {

        holder.nameTextView.text = skillList[position].name
        holder.descriptionTextView.text = skillList[position].description

        val skillIconStorageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.SKILL_ICONS.toString())
                .child(skillList[position].id)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(skillIconStorageRef)
                .error(R.drawable.ic_default_skill)
                .into(holder.iconImageView)

        holder.toggleSwitch.isChecked = userSkills.contains(skillList[position].id)

        holder.toggleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(buttonView.isPressed) {
                val skillDbRef = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.SKILLS_BY_USER.toString())
                        .child(FirebaseAuth.getInstance().uid!!)
                        .child(skillList[position].id)

                when {
                    isChecked -> {
                        // User just stated they have skill
                        skillDbRef.setValue(true)
                    }
                    else -> {
                        // User just stated they do not have this skill
                        skillDbRef.removeValue()
                    }
                }
            }
        }
    }

    inner class SkillToggleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.skill_name
        val descriptionTextView: TextView = itemView.skill_description
        val iconImageView: AppCompatImageView = itemView.skill_icon
        val toggleSwitch: Switch = itemView.skill_switch
    }

    fun setSkills(skillList: List<Skill>) {
        this.skillList = skillList
        notifyDataSetChanged()
    }
}