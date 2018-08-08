package bab.com.build_a_bridge.adapters

import android.content.Context
import bab.com.build_a_bridge.AcceptRequestFragment
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.RequestDetailsFragment
import bab.com.build_a_bridge.objects.Request

class RequestsFragmentAdapter(requestList: ArrayList<Request>, context: Context, activity: MainActivity) :
RequestAdapter(requestList, context, activity){
    override fun onItemClick(position: Int) {
        if(requestList[position].volunteerId == null) {
            // This is a request which has no volunteer attached
            activity.viewModel.requestForAccept = requestList[position]
            activity.swapFragments(AcceptRequestFragment(), true)
        }else {
            // This request has a volunteer attached
            activity.viewModel.requestForDetails = requestList[position]
            activity.swapFragments(RequestDetailsFragment(), true)
        }

    }
}