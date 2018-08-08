package bab.com.build_a_bridge


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.adapters.MessageListAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.Message
import bab.com.build_a_bridge.objects.TimeStamp
import bab.com.build_a_bridge.utils.FirebaseMessagingLiveDataList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_messaging.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class MessagingFragment : Fragment(), AnkoLogger{

    lateinit var messagingLiveData: FirebaseMessagingLiveDataList

    lateinit var messageListAdapter: MessageListAdapter
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messaging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = ""

        // Adapter setup
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        messaging_rv.layoutManager = layoutManager
        messaging_rv.setHasFixedSize(true)
        messageListAdapter = MessageListAdapter(context!!, viewModel.user, viewModel.userToMessage)
        messaging_rv.adapter = messageListAdapter

        val messageDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.MESSAGES.toString())
                .child(viewModel.messageId)

        info { "HERE" }
        info { viewModel.userToMessage.toString() }
        info { viewModel.messageId }

        messagingLiveData = FirebaseMessagingLiveDataList(messageDbRef)

        messagingLiveData.observe(this, Observer {
            it?.let {
                messageListAdapter.setMessages(it)
            }
        })

        send_message_btn.setOnClickListener {
            val messageContent = message_content_et.text.toString()
            val message = Message(content = messageContent,
                    senderUid = viewModel.user.userId,
                    timeStamp = TimeStamp.getInstance())

            // clear the edit text
            message_content_et.text.clear()

            val conversationDbRef = FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.MESSAGES.toString())
                    .child(viewModel.messageId)

            conversationDbRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    // Do nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val conversation = dataSnapshot.getValue(Conversation::class.java)
                    conversation?.let {
                        val messageList = it.messageHistory as ArrayList
                        messageList.add(message)
                        conversation.messageHistory = messageList

                        conversationDbRef.setValue(conversation)
                    }

                }

            })
        }

    }


}
