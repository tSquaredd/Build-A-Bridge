package bab.com.build_a_bridge.adapters

import android.content.Context
import bab.com.build_a_bridge.presentation.AcceptRequestFragment
import bab.com.build_a_bridge.presentation.MainActivity
import bab.com.build_a_bridge.presentation.RequestDetailsFragment
import bab.com.build_a_bridge.objects.Request

/**
 * Extension of RequestAdapter to add appropriate implementation of onItemClick for
 * the RequestFragment Adapter.
 */
class RequestsFragmentAdapter(requestList: ArrayList<Request>,  userId: String, context: Context, activity: MainActivity) :
        ActiveRequestAdapter(requestList, userId,  context, activity) {
    override fun onItemClick(position: Int) {
        if (requestList[position].volunteerId == null) {
            // This is a request which has no volunteer attached
            activity.viewModel.requestForAccept = requestList[position]
            activity.swapFragments(AcceptRequestFragment(), true)
        } else {
            // This request has a volunteer attached
            activity.viewModel.requestForDetails = requestList[position]
            activity.swapFragments(RequestDetailsFragment(), true)
        }
    }
}