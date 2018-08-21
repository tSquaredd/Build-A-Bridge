package bab.com.build_a_bridge.adapters

import bab.com.build_a_bridge.enums.FirebaseDbNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Used in SkillsFragment for a User to select which skills they have.
 */
class SkillSelectionToggleAdapter(private val userSkills: ArrayList<String>) : SkillToggleAdapter(){

    override fun skillToggled(isSelected: Boolean, position: Int) {
        val skillDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS_BY_USER.toString())
                .child(FirebaseAuth.getInstance().uid!!)
                .child(skillList[position].id)

        when {
            isSelected -> {
                // User just stated they have skill
                skillDbRef.setValue(true)
            }
            else -> {
                // User just stated they do not have this skill
                skillDbRef.removeValue()
            }
        }
    }

    override fun onBindViewHolder(holder: SkillToggleHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.toggleSwitch.isChecked = userSkills.contains(skillList[position].id)
    }
}