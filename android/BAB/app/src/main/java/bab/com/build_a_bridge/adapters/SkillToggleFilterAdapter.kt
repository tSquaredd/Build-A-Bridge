package bab.com.build_a_bridge.adapters

import bab.com.build_a_bridge.objects.Skill
import kotlin.collections.ArrayList

class SkillToggleFilterAdapter : SkillToggleAdapter() {

    var selectedSkillsList = arrayListOf<Skill>()

    override fun skillToggled(isSelected: Boolean, position: Int) {
        when (isSelected) {
            true -> {
                selectedSkillsList.add(skillList[position])
            }
            false -> {
                selectedSkillsList.removeAt(selectedSkillsList.indexOf(skillList[position]))
            }
        }
    }

    override fun onBindViewHolder(holder: SkillToggleHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (selectedSkillsList.isEmpty()) {
            selectedSkillsList = skillList.toMutableList() as ArrayList<Skill>
        }

        holder.toggleSwitch.isChecked = selectedSkillsList.contains(skillList[position])

    }

    fun setSelectedSkills(skillList: ArrayList<Skill>) {
        this.selectedSkillsList = skillList
        notifyDataSetChanged()
    }
}