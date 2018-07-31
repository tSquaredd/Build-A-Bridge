package bab.com.build_a_bridge.adapters

import android.content.Context
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.objects.Skill


/**
 * Adapter for skill items extended from the base skill adapter, SkillAdapter.
 * Implements on click functionality for skill items, and sets the view models
 * new request object with the skill selected.
 */
class SkillSelectionAdapter(skillList: ArrayList<Skill>, context: Context, val activity: MainActivity) :
        SkillAdapter(skillList, context) {
    override fun onItemClick(position: Int) {
        //val viewModel = ViewModelProviders.of(activity).get(MainActivityViewModel::class.java)

        // set skill for request in view model
        activity.viewModel.newRequest?.skillRequested = skillList[position]

        // return to calling fragment
        activity.onBackPressed()
    }
}