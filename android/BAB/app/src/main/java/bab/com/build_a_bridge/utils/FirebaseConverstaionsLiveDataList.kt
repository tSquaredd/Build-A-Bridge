package bab.com.build_a_bridge.utils

import android.arch.lifecycle.LiveData
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class FirebaseConverstaionsLiveDataList :
        LiveData<List<Conversation>>() {
    private val listener: ConversationValueEventListener
    private val databaseReference: DatabaseReference

    init {
        listener = ConversationValueEventListener()
        databaseReference = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.MESSAGES_BY_USER.toString())
                .child(FirebaseAuth.getInstance().uid!!)
    }

    override fun onActive() {
        databaseReference.addValueEventListener(listener)
    }

    override fun onInactive() {
        databaseReference.removeEventListener(listener)
    }


    private inner class ConversationValueEventListener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            // Do nothing
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val conversationList = arrayListOf<Conversation>()
            val messageUserIds = dataSnapshot.children
            val numConversations = dataSnapshot.childrenCount
            var counter: Long = 0
            for(userId in messageUserIds){
                val msgId = userId.getValue(String::class.java)
                msgId?.let {

                    // get conversation from MESSAGES
                    val msgDbRef = FirebaseDatabase.getInstance().reference
                            .child(FirebaseDbNames.MESSAGES.toString())
                            .child(it)

                    msgDbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            // Do nothing
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            counter++
                            val conversation = dataSnapshot.getValue(Conversation::class.java)
                            conversation?.let { conversationList.add(it) }

                            if(counter == numConversations)
                                value = conversationList

                        }

                    })
                }
            }
        }
    }
}