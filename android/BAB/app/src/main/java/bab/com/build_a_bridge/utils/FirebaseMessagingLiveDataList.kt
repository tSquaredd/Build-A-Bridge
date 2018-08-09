package bab.com.build_a_bridge.utils

import android.arch.lifecycle.LiveData
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Wraps a List<Message> in LiveData to observe changes to the databaseReference.
 * When a change occurs the data is parsed into a list of Messages, and sent
 * to the observer.
 */
class FirebaseMessagingLiveDataList(private val databaseReference: DatabaseReference) :
        LiveData<List<Message>>() {
    private val listener: MessagingValueEventLisener

    override fun onActive() {
        databaseReference.addValueEventListener(listener)
    }

    override fun onInactive() {
        databaseReference.removeEventListener(listener)
    }

    init {
        listener = MessagingValueEventLisener()
    }

    private inner class MessagingValueEventLisener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            // Do nothing
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {

            var messageList = listOf<Message>()
            val conversation = dataSnapshot.getValue(Conversation::class.java)
            conversation?.let {
                messageList = it.messageHistory
            }
            messageList.sortedBy { it.timeStamp }

            value = messageList
        }
    }
}