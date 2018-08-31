package bab.com.build_a_bridge.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.adapters.MessageListAdapter
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.Message
import bab.com.build_a_bridge.objects.TimeStamp
import bab.com.build_a_bridge.utils.FirebaseMessagingLiveDataList
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_messaging.*

/**
 * Handles messaging between two users
 */
class MessagingFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    private lateinit var messagingLiveData: FirebaseMessagingLiveDataList
    private lateinit var messageListAdapter: MessageListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messaging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = ""

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Adapter setup
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        messaging_rv.layoutManager = layoutManager
        messaging_rv.setHasFixedSize(true)
        messageListAdapter = MessageListAdapter(context!!, viewModel.user, viewModel.userToMessage)
        messaging_rv.adapter = messageListAdapter

        val messageDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.MESSAGES.toString())
                .child(viewModel.messageId)

        messagingLiveData = FirebaseMessagingLiveDataList(messageDbRef)


        messagingLiveData.observe(this, Observer {
            it?.let { messageList ->
                messageListAdapter.handleObservation(messageList)
                messaging_rv.scrollToPosition(messageList.size - 1)
            }
        })

        send_message_btn.setOnClickListener {


            // hide keyboard
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(it.windowToken, 0 )

            val senderDisplayName = "${viewModel.user.firstName} ${viewModel.user.lastName}"

            val messageContent = message_content_et.text.toString()
            val message = Message(content = messageContent,
                    senderUid = viewModel.user.userId,
                    senderName = senderDisplayName,
                    receiverUid = viewModel.userToMessage.userId,
                    receiverFcmToken = viewModel.userToMessage.fcmToken,
                    timeStamp = TimeStamp.getInstance())

            // clear the edit text
            message_content_et.text.clear()


            val conversationDbRef = FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.MESSAGES.toString())
                    .child(viewModel.messageId)

            conversationDbRef.runTransaction(object : Transaction.Handler{
                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                   // Update msgId in MESSAGES_BY_USER to same ID. This is just to push update out to listeners
                    val conversation = p2?.getValue(Conversation::class.java)
                    conversation?.let { convo ->
                        val otherUser = if(convo.uid1 == viewModel.user.userId) convo.uid2 else convo.uid1

                        val msgDbRef = FirebaseDatabase.getInstance().reference
                                .child(FirebaseDbNames.MESSAGES_BY_USER.toString())
                                .child(viewModel.user.userId)
                                .child(otherUser)


                        msgDbRef.setValue(convo.msgId)
                    }


                }

                override fun doTransaction(data: MutableData): Transaction.Result {
                    val conversation = data.getValue(Conversation::class.java)
                    conversation?.let {
                        val messageList = it.messageHistory as ArrayList
                        messageList.add(message)
                        conversation.messageHistory = messageList
                        conversation.lastUpdated = TimeStamp.getInstance()

                        conversationDbRef.setValue(conversation)
                    }

                    return Transaction.abort()
                }

            })
        }

        setupBottomActionBar()
    }

    private fun setupBottomActionBar(){
        activity?.fab?.hide()
    }
}
