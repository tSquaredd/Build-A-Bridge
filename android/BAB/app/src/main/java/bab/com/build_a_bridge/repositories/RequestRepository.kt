package bab.com.build_a_bridge.repositories

import android.arch.lifecycle.MutableLiveData
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.utils.FirebaseDbRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RequestRepository {
    private val allRequests = MutableLiveData<List<Request>>()

    fun loadAllRequestsFor(state: String, region: String){
        val dbRef = FirebaseDbRefUtil.getRequestsInRegion(state, region)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val requestList = arrayListOf<Request>()
                val requests = dataSnapshot.children
                for(d: DataSnapshot in requests){
                    val request = d.getValue(Request::class.java)
                    request?.let { nonNullRequest ->
                        requestList.add(nonNullRequest)
                    }
                }

                allRequests.value = requestList

            }

        })
    }

    fun getRequests(): MutableLiveData<List<Request>>{
        return allRequests
    }
}