package bab.com.build_a_bridge.adapters

import android.content.Context
import android.os.Bundle
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.enums.BundleParamNames
import bab.com.build_a_bridge.objects.Skill

class AdminSkillAdapter(skillList: ArrayList<Skill>, context: Context, val mainActivity: MainActivity) :
        SkillAdapter(skillList, context){
    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(BundleParamNames.SKILL.toString(), skillList[position])
        bundle.putInt(BundleParamNames.SKILL_LIST_INDEX.toString(), position)
        val fragment = AdminEditSkillsFragment()
        fragment.arguments = bundle
        mainActivity.swapFragments(fragment)
    }


}