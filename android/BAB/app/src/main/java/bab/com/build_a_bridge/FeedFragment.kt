package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.RequestAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Request
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_feed.*



class FeedFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.feed)
        feed_fab.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.swapFragments(CreateRequestFragment())
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        request_feed_rv.layoutManager = layoutManager

        getRequestFeedList()

    }

    /**
     * Initializes adapter and hides progress bar
     */
    private fun setAdapter() {
        feed_progress_bar.visibility = View.GONE
        request_feed_rv.visibility = View.VISIBLE
        request_feed_rv.adapter = RequestAdapter(viewModel.requestFeedList, context!!)
    }


    /**
     * Gets all requests with status REQUESTED in users state/region that are not
     * requests of the user themselves
     */
    private fun getRequestFeedList() {

        // empty the list of requests
        viewModel.requestFeedList = arrayListOf()
        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(viewModel.user?.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(viewModel.user?.region.toString())
                .child(RequestStatusCodes.REQUESTED.toString())

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val requests = p0.children
                for (d: DataSnapshot in requests) {
                    val request = d.getValue(Request::class.java)
                    request?.let {
                        if (it.requestId != viewModel.user?.userId)
                            viewModel.requestFeedList.add(it)
                    }

                }

                if (!viewModel.requestFeedList.isEmpty())
                    setAdapter()
                else {
                    feed_progress_bar.visibility = View.GONE
                    feed_no_items_tv.visibility = View.VISIBLE
                }
            }
        })
    }
}
