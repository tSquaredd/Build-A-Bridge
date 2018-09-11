package bab.com.build_a_bridge.presentation


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
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.RequestsFragmentAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.utils.FirebaseDbRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
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


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)

        requests_fragment_rv.layoutManager = layoutManager

        // Listener for pull down refresh
        request_fragment_swipe_layout.setOnRefreshListener {
            getRequestsList()
        }

        // Sets color of the refresh wheel
        request_fragment_swipe_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        getRequestsList()

        setupBottomAppBar()

    }


    private fun getRequestsList() {
        // empty the list of requests
        viewModel.requestsFragmentList = arrayListOf()
        val db = FirebaseDbRefUtil.getAllRequestsByUserRef(viewModel.user.userId)


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
        requests_fragment_rv.adapter = RequestsFragmentAdapter(viewModel.requestsFragmentList,
                viewModel.user.userId, context!!, activity as MainActivity)
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
