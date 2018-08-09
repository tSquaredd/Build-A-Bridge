package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.RequestsFragmentAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Request
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_requests.*

/**
 * Displays User's current accepted and requested requests
 */
class RequestsFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.requests)
        requests_fab.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.swapFragments(CreateRequestFragment(), true)
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        requests_fragment_rv.addItemDecoration(divider)
        requests_fragment_rv.layoutManager = layoutManager

        // Listener for pull down refresh
        request_fragment_swipe_layout.setOnRefreshListener {
            getRequestsList()
        }

        // Sets color of the refresh wheel
        request_fragment_swipe_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        getRequestsList()

    }


    private fun getRequestsList() {
        // empty the list of requests
        viewModel.requestsFragmentList = arrayListOf()
        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                .child(viewModel.user.userId)


        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val requestCodeChildren = dataSnapshot.children
                for (snapshot in requestCodeChildren) {
                    val requestChildren = snapshot.children
                    for (requestChild in requestChildren) {
                        val request = requestChild.getValue(Request::class.java)
                        viewModel.requestsFragmentList.add(request!!)
                    }
                }

                if (!viewModel.requestsFragmentList.isEmpty())
                    setAdapter()
                else {
                    requests_progress_bar.visibility = View.GONE
                    requests_no_items_tv.visibility = View.VISIBLE
                }
            }
        })

        request_fragment_swipe_layout.isRefreshing = false
    }

    private fun setAdapter() {
        requests_progress_bar.visibility = View.GONE
        requests_fragment_rv.visibility = View.VISIBLE
        requests_fragment_rv.adapter = RequestsFragmentAdapter(viewModel.requestsFragmentList, context!!, activity as MainActivity)
    }
}
