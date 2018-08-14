package bab.com.build_a_bridge.objects

/**
 * Models a single message transaction from one user to another
 */
data class Message(val content: String = "",
                   val senderUid: String = "",
                   val senderName: String = "",
                   val receiverUid: String = "",
                   val receiverFcmToken: String = "",
                   val timeStamp: TimeStamp = TimeStamp.getInstance())