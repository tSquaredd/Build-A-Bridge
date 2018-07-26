package bab.com.build_a_bridge.adapters

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.admin.AdminEditSkillsFragment
import bab.com.build_a_bridge.enums.BundleParamNames
import bab.com.build_a_bridge.objects.Skill
import kotlinx.android.synthetic.main.skill_list_item.view.*

class SkillAdapter(skillList: ArrayList<Skill>, activity: Activity) : RecyclerView.Adapter<SkillAdapter.SkillHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SkillHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.skill_list_item, parent, false)
        return SkillHolder(view)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        holder.skillNameTextView.text = skillList[position].name
        holder.skillDescriptionTextView.text = skillList[position].description
    }

    val activity = activity
    val skillList = skillList

    inner class SkillHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillNameTextView = itemView.skill_name
        val skillDescriptionTextView = itemView.skill_description

        init {
            itemView.setOnClickListener {
                val mainActivity = activity as MainActivity
                val bundle = Bundle()
                bundle.putString(BundleParamNames.SKILL_ID.toString(), skillList[adapterPosition].skillId.toString())
                bundle.putString(BundleParamNames.SKILL_NAME.toString(), skillList[adapterPosition].name)
                bundle.putString(BundleParamNames.SKILL_DESCRIPTION.toString(), skillList[adapterPosition].description)
                bundle.putInt(BundleParamNames.SKILL_LIST_INDEX.toString(), adapterPosition)
                val fragment = AdminEditSkillsFragment()
                fragment.arguments = bundle
                mainActivity.swapFragments(fragment)
            }
        }
    }
}