package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
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
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.SkillToggleFilterAdapter
import bab.com.build_a_bridge.objects.Skill
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed_skill_filter.*
import org.jetbrains.anko.toast
import java.util.*


class FeedSkillFilterFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    lateinit var skillAdapter: SkillToggleFilterAdapter
    private val allSkillsList = arrayListOf<Skill>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_skill_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup bottom app bar
        setupBottomAppBar()
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



    }

    private fun setupBottomAppBar(){
        activity?.fab?.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                activity?.bottom_app_bar?.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                activity?.fab?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_done))
                

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
