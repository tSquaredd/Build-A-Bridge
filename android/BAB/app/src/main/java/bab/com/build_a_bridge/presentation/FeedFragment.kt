package bab.com.build_a_bridge.presentation



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
import android.view.*
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.R.id.action_filter_feed
import bab.com.build_a_bridge.adapters.FeedFragmentAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*



/**
 * Shows a list of available requests that a User can sign up for.
 */
class FeedFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.feed)

        setupBottomAppBar()


        // Setup RV
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        feed_fragment_rv.addItemDecoration(divider)
        feed_fragment_rv.layoutManager = layoutManager

        // Listener for pull down refresh
        feed_feagment_swipe_layout.setOnRefreshListener {
            viewModel.refreshRequestList()
        }

        // Sets color of the refresh wheel
        feed_feagment_swipe_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.feed, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when (id) {
            action_filter_feed -> {
                (activity as MainActivity).swapFragments(FeedSkillFilterFragment(), true)
            }
            else -> {
                // Do nothing
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRequests().observe(this, Observer{
            it?.let {requestList ->
                viewModel.filterRequests(requestList)
                setAdapter()

            }
        })
    }

    private fun setupBottomAppBar() {
        if(activity?.fab?.isOrWillBeHidden!!){
            activity?.fab?.show()
        } else {
            activity?.fab?.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    activity?.bottom_app_bar?.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
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

    /**
     * Initializes adapter and hides progress bar
     */
    private fun setAdapter() {
        feed_feagment_swipe_layout.isRefreshing = false
        feed_progress_bar.visibility = View.GONE
        feed_fragment_rv.visibility = View.VISIBLE
        feed_fragment_rv.adapter = FeedFragmentAdapter(viewModel.feedFragmentList, context!!, activity as MainActivity)

        if(viewModel.feedFragmentList.isEmpty()){
            feed_no_items_tv.visibility = View.VISIBLE
            feed_fragment_rv.visibility = View.INVISIBLE
        } else {
            feed_fragment_rv.visibility = View.VISIBLE
            feed_no_items_tv.visibility = View.INVISIBLE
        }
    }



}
