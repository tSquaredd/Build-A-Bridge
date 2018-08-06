package bab.com.build_a_bridge.adapters

import android.content.Context
import bab.com.build_a_bridge.AcceptRequestFragment
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.objects.Request

class FeedFragmentAdapter(requestList: ArrayList<Request>, context: Context, activity: MainActivity) :
        RequestAdapter(requestList, context, activity) {
    override fun onItemClick(position: Int) {
        activity.viewModel.requestForAccept = requestList[position]
        activity.swapFragments(AcceptRequestFragment(), true)
    }
}