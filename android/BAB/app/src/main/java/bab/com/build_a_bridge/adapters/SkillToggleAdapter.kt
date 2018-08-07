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


class SkillToggleAdapter(private val userSkills: ArrayList<String>):
        RecyclerView.Adapter<SkillToggleAdapter.SkillTogglerHolder>(){

    var skillList: List<Skill> = listOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SkillTogglerHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.skill_toggle_item, parent, false)
        return SkillTogglerHolder(view)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: SkillTogglerHolder, position: Int) {

        holder.skillNameTextView.text = skillList[position].name
        holder.skillDescriptionTextView.text = skillList[position].description

        val skillIconStorageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.SKILL_ICONS.toString())
                .child(skillList[position].id)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(skillIconStorageRef)
                .into(holder.skillIcon)

        holder.toggle.isChecked = userSkills.contains(skillList[position].id)

        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            val skillDbRef = FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.SKILLS_BY_USER.toString())
                    .child(FirebaseAuth.getInstance().uid!!)
                    .child(skillList[position].id)

            when{

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

    inner class SkillTogglerHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val skillNameTextView: TextView = itemView.skill_name
        val skillDescriptionTextView: TextView = itemView.skill_description
        val skillIcon: AppCompatImageView = itemView.skill_icon
        val toggle: Switch = itemView.skill_switch

    }

    fun setSkills(skillList: List<Skill>){
        this.skillList = skillList
        notifyDataSetChanged()
    }
}