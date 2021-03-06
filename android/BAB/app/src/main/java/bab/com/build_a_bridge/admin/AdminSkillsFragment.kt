package bab.com.build_a_bridge.admin


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.presentation.MainActivity
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.AdminSkillAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin_skills.*

/**
 * Fragment that is only viewable for admins of the system. This Fragment allows the admin
 * to see the list of all skills. By clicking a Skill they launch AdminEditSkillsFragment
 * where they can edit the Skill and / or icon  for the Skill.
 */
class AdminSkillsFragment : Fragment() {

    lateinit var skillAdapter: AdminSkillAdapter
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
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        admin_skills_recycler_view.layoutManager = layoutManager
        admin_skills_recycler_view.addItemDecoration(divider)
        admin_skills_recycler_view.setHasFixedSize(true)
        skillAdapter = AdminSkillAdapter(context!!, activity as MainActivity)
        admin_skills_recycler_view.adapter = skillAdapter

        viewModel.skillLiveDataList.observe(this, Observer {
            it?.let { skillList ->
                if (skillList.isEmpty()) {
                    empty_admin_skills_list_text_view.visibility = View.VISIBLE
                } else {
                    skillAdapter.setSkills(skillList)
                    empty_admin_skills_list_text_view.visibility = View.GONE
                }

            }
        })

        setupBottomAppBar()
    }

    private fun setupBottomAppBar() {
        if(activity?.fab?.isOrWillBeHidden!!){
            activity?.fab?.show()
        } else {
            activity?.fab?.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    activity?.bottom_app_bar?.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    activity?.fab?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_add))
                    val handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            activity?.fab?.show()
                        }

                    }, 200)
                }
            })
        }
    }
}
