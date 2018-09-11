package bab.com.build_a_bridge.utils

import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.RegionCodes
import bab.com.build_a_bridge.enums.RequestStatusCodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDbRefUtil {
    companion object {

        /**
         * Gets DatabaseReference to all REQUESTSED Request objects in a region.
         */
        fun getRequestsInRegion(state: String, region: String): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.REQUESTS.toString())
                    .child(FirebaseDbNames.STATE.toString())
                    .child(state)
                    .child(FirebaseDbNames.REGION.toString())
                    .child(region)
                    .child(RequestStatusCodes.REQUESTED.toString())
        }

        /**
         * Gets a DatabaseReference to a Request object within REQUESTS_BY_USER
         */
        fun getRequestByUserRefWithStatusCode(userId: String, requestId: String, statusCode: RequestStatusCodes): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                    .child(userId)
                    .child(statusCode.toString())
                    .child(requestId)

        }

        fun getAllRequestsByUserRef(userId: String): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                    .child(userId)
        }

        /**
         * Gets a DatabaseReference to a Request object within REQUESTS
         */
        fun getRequestRef(state: String, region: String, statusCode: RequestStatusCodes, requestId: String)
                : DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.REQUESTS.toString())
                    .child(FirebaseDbNames.STATE.toString())
                    .child(state)
                    .child(FirebaseDbNames.REGION.toString())
                    .child(region)
                    .child(statusCode.toString())
                    .child(requestId)
        }

        /**
         * Gets DatabaseReference to User information in USER_ID_DIRECTORY
         */
        fun getUserRef(userId: String): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                    .child(userId)
        }

        /**
         * DB structure is
         * MESSAGES_BY_USER -> userId -> targetUserId
         *
         * Where target user is the target for the message.
         * This is used primarily to see if the target user exists in the users messages
         *
         * If so the user and target user have a message history, and if not they dont
         *
         * When getting the value from this reference it will be the msg id which can
         * be used to get the Conversation object from MESSAGES
         */
        fun getMsgByUserTargetUserDbRef(userId: String, targetUserId: String): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.MESSAGES_BY_USER.toString())
                    .child(userId)
                    .child(targetUserId)
        }

        fun getMsgDbRef(msgId: String): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.MESSAGES.toString())
                    .child(msgId)
        }
    }
}