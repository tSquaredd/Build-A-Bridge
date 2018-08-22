package bab.com.build_a_bridge.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.SkillSelectionAdapter
import bab.com.build_a_bridge.objects.Skill
import kotlinx.android.synthetic.main.fragment_skill_selection.*

/**
 * Used to choose a skill for a new request
 */
class SkillSelectionFragment : Fragment() {
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    private lateinit var skillAdapter: SkillSelectionAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skill_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.select_skill)
        skillAdapter = SkillSelectionAdapter(context!!, activity as MainActivity)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        skill_selection_rv.addItemDecoration(divider)
        skill_selection_rv.layoutManager = layoutManager
        skill_selection_rv.setHasFixedSize(true)
        skill_selection_rv.adapter = skillAdapter

        viewModel.skillLiveDataList.observe(this, Observer { skillList: List<Skill>? ->
            skillList?.let { nonNullSkillList ->
                skillAdapter.setSkills(nonNullSkillList.sortedBy { it.name.toUpperCase() })
            }
        })
    }
}
