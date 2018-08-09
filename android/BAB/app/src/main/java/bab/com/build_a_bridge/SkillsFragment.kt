package bab.com.build_a_bridge


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.SkillToggleAdapter
import kotlinx.android.synthetic.main.fragment_skills.*

/**
 * Fragment that allows user to edit the skills that they offer
 */
class SkillsFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    lateinit var skillToggleAdapter: SkillToggleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.skills)

        // Adapter setup
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        skills_recycler_view.layoutManager = layoutManager
        skills_recycler_view.addItemDecoration(divider)
        skills_recycler_view.setHasFixedSize(true)
        skillToggleAdapter = SkillToggleAdapter(viewModel.userSkillsList)
        skills_recycler_view.adapter = skillToggleAdapter

        viewModel.skillLiveDataList.observe(this, Observer {
            it?.let {skillList ->
                if(skillList.isEmpty()) skills_empty_list_tv.visibility = View.VISIBLE
                else {
                    skills_empty_list_tv.visibility = View.GONE
                    skillToggleAdapter.setSkills(skillList.sortedBy {skill ->
                        skill.name.toUpperCase() })
                }
            }
        })
    }
}
