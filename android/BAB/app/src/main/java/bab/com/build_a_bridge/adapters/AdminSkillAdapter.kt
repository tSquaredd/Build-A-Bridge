package bab.com.build_a_bridge.adapters

import android.content.Context
import android.os.Bundle
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.enums.BundleParamNames

class AdminSkillAdapter( context: Context, val mainActivity: MainActivity) :
        SkillAdapter(context){
    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        val skillList = mainActivity.viewModel.skillLiveDataList.value?.sortedBy { it.name.toUpperCase() }
        skillList?.let {
            bundle.putParcelable(BundleParamNames.SKILL.toString(), it[position])
        }

        val fragment = AdminEditSkillsFragment()
        fragment.arguments = bundle
        mainActivity.swapFragments(fragment, true)
    }


}