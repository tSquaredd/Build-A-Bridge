package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import bab.com.build_a_bridge.adapters.FeedFragmentAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Request
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        feed_fab.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.swapFragments(CreateRequestFragment(), true)
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        feed_fragment_rv.addItemDecoration(divider)
        feed_fragment_rv.layoutManager = layoutManager

        // Listener for pull down refresh
        feed_feagment_swipe_layout.setOnRefreshListener {
            getRequestFeedList()
        }

        // Sets color of the refresh wheel
        feed_feagment_swipe_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        getRequestFeedList()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.feed, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Initializes adapter and hides progress bar
     */
    private fun setAdapter() {
        feed_progress_bar.visibility = View.GONE
        feed_fragment_rv.visibility = View.VISIBLE
        feed_fragment_rv.adapter = FeedFragmentAdapter(viewModel.feedFragmentList, context!!, activity as MainActivity)
    }


    /**
     * Gets all requests with status REQUESTED in users state/region that are not
     * requests of the user themselves
     */
    private fun getRequestFeedList() {

        // empty the list of requests
        viewModel.feedFragmentList = arrayListOf()
        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(viewModel.user.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(viewModel.user.region.toString())
                .child(RequestStatusCodes.REQUESTED.toString())

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val requests = dataSnapshot.children
                for (d: DataSnapshot in requests) {
                    val request = d.getValue(Request::class.java)
                    request?.let {
                        if (it.requesterId != viewModel.user.userId)
                            viewModel.feedFragmentList.add(it)
                    }
                }

                if (!viewModel.feedFragmentList.isEmpty()) {
                    setAdapter()
                } else {
                    feed_progress_bar.visibility = View.GONE
                    feed_no_items_tv.visibility = View.VISIBLE
                }
            }
        })

        feed_feagment_swipe_layout.isRefreshing = false
    }
}
