package bab.com.build_a_bridge.admin


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.SkillAdapter
import kotlinx.android.synthetic.main.fragment_admin_skills.*

class AdminSkillsFragment : Fragment() {

    lateinit var skillAdapter: SkillAdapter
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.skills_admin)

        // Adapter setup
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)
        admin_skills_recycler_view.layoutManager = layoutManager
        admin_skills_recycler_view.setHasFixedSize(true)
        skillAdapter = SkillAdapter(viewModel.systemSkillsList, activity!!, context!!)
        admin_skills_recycler_view.adapter = skillAdapter


        // FAB
        admin_skills_fab.setOnClickListener {
            val activity = activity as MainActivity
            activity.swapFragments(AdminEditSkillsFragment())
        }

        if(viewModel.systemSkillsList.isEmpty())
            empty_admin_skills_list_text_view.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.systemSkillsList.isEmpty()) empty_admin_skills_list_text_view.visibility = View.VISIBLE
        else empty_admin_skills_list_text_view.visibility = View.INVISIBLE
    }
}
