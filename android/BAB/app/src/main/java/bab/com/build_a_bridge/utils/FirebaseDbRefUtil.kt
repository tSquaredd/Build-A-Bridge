package bab.com.build_a_bridge.utils

import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.RegionCodes
import bab.com.build_a_bridge.enums.RequestStatusCodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDbRefUtil {
    companion object {
        fun requestsInRegion(state: String, region: String): DatabaseReference{
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.REQUESTS.toString())
                    .child(FirebaseDbNames.STATE.toString())
                    .child(state)
                    .child(FirebaseDbNames.REGION.toString())
                    .child(region)
                    .child(RequestStatusCodes.REQUESTED.toString())
        }
    }
}