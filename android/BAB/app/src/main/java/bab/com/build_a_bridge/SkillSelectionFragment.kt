package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.SkillSelectionAdapter
import kotlinx.android.synthetic.main.fragment_skill_selection.*


class SkillSelectionFragment : Fragment(){
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skill_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.select_skill)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        skill_selection_rv.layoutManager = layoutManager
        skill_selection_rv.setHasFixedSize(true)
        skill_selection_rv.adapter = SkillSelectionAdapter(viewModel.systemSkillsList, context!!, activity as MainActivity)
    }

}
