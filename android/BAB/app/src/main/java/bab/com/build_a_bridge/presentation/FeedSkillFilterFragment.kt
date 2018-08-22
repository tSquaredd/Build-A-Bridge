package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.SkillToggleFilterAdapter
import bab.com.build_a_bridge.objects.Skill
import kotlinx.android.synthetic.main.fragment_feed_skill_filter.*
import org.jetbrains.anko.toast
import java.util.*


class FeedSkillFilterFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    private lateinit var skillAdapter: SkillToggleFilterAdapter
    private val allSkillsList = arrayListOf<Skill>()

//    companion object {
//        const val ARG_SKILL_LIST = "skill_list"
//        const val EXTRA_SKILL_LIST = "bab.com.build_a_bridge.skill_list"
//
//        fun newInstance(selectedSkillsList: ArrayList<Skill>): FeedSkillFilterFragment {
//            val args = Bundle()
//            args.putParcelableArrayList(ARG_SKILL_LIST, selectedSkillsList)
//            val fragment = FeedSkillFilterFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_skill_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            //filterSkillsList = it.getParcelableArrayList(ARG_SKILL_LIST)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                    false)
            val divider = DividerItemDecoration(context, layoutManager.orientation)
            skill_filter_rv.layoutManager = layoutManager
            skill_filter_rv.addItemDecoration(divider)
            skill_filter_rv.setHasFixedSize(true)
            skillAdapter = SkillToggleFilterAdapter()
            skill_filter_rv.adapter = skillAdapter

            viewModel.skillLiveDataList.observe(this, Observer {
                it?.let { skillList ->
                    if (skillList.isEmpty()) // TODO ADD EMPTY VIEW
                    else {
                        allSkillsList.clear()
                        allSkillsList.addAll(skillList)
                        skillAdapter.setSkills(skillList.sortedBy { skill ->
                            skill.name.toUpperCase()
                        })
                        skillAdapter.setSelectedSkills(viewModel.feedSkillFilterList)
                    }
                }
            })


        filter_done_btn.setOnClickListener {
            if(skillAdapter.selectedSkillsList.isEmpty()){
                activity?.toast("Must select one skill")
            }else {
                viewModel.feedSkillFilterList = skillAdapter.selectedSkillsList
                activity?.onBackPressed()
            }
//            sendResult(Activity.RESULT_OK)

        }
    }

//    private fun sendResult(resultCode: Int) {
//        if (targetFragment == null) {
//            return
//        }
//
//        val intent = Intent()
//        intent.putExtra(EXTRA_SKILL_LIST, skillAdapter.selectedSkillsList)
//        activity?.onBackPressed()
//        targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
//    }
}
