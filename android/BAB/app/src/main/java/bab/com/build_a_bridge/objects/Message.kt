package bab.com.build_a_bridge.objects

data class Message(val content: String = "", val senderUid: String = "", val timeStamp: TimeStamp = TimeStamp.getInstance())