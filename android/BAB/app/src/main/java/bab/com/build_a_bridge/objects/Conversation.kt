package bab.com.build_a_bridge.objects

data class Conversation(val uid1: String = "",
                        val uid2: String = "",
                        val msgId: String = "",
                        var messageHistory: List<Message> = arrayListOf(),
                        var lastUpdated: TimeStamp = TimeStamp.getInstance()) {


}