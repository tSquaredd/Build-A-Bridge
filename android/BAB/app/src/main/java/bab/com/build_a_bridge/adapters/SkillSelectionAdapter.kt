package bab.com.build_a_bridge.adapters


import android.content.Context
import bab.com.build_a_bridge.presentation.MainActivity

/**
 * Adapter for skill items extended from the base skill adapter, SkillAdapter.
 * Implements on click functionality for skill items, and sets the View models
 * new request object with the skill selected.
 */
class SkillSelectionAdapter(context: Context, val activity: MainActivity) :
        SkillAdapter(context) {
    override fun onItemClick(position: Int) {
        // get list of skills sorted by letter since adapter sorts its display list
        val skillList = activity.viewModel.skillLiveDataList.value?.sortedBy { it.name.toUpperCase() }
        skillList?.let {
            // set skill for request in View model
            activity.viewModel.newRequest?.skillId = it[position].id

            // return to calling fragment
            activity.onBackPressed()
        }
    }
}