package bab.com.build_a_bridge.adapters

import bab.com.build_a_bridge.objects.Skill
import kotlin.collections.ArrayList

class SkillToggleFilterAdapter : SkillToggleAdapter() {

    var selectedSkillsList = arrayListOf<String>()

    override fun skillToggled(isSelected: Boolean, position: Int) {
        when (isSelected) {
            true -> {
                selectedSkillsList.add(skillList[position].id)
            }
            false -> {
                selectedSkillsList.removeAt(selectedSkillsList.indexOf(skillList[position].id))
            }
        }
    }

    override fun onBindViewHolder(holder: SkillToggleHolder, position: Int) {
        super.onBindViewHolder(holder, position)
//        if (selectedSkillsList.isEmpty()) {
//            selectedSkillsList = skillList.toMutableList() as ArrayList<String>
//        }

        holder.toggleSwitch.isChecked = selectedSkillsList.contains(skillList[position].id)

    }

    fun setSelectedSkills(skillList: ArrayList<String>) {
        this.selectedSkillsList = skillList
        notifyDataSetChanged()
    }
}