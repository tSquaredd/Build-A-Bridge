package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Message
import bab.com.build_a_bridge.objects.User
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_received_item.view.*
import kotlinx.android.synthetic.main.message_sent_item.view.*

/**
 * Adapter used to view a Conversation's Message's.
 *
 * Determines if a message is of type sent or receieved and inflates and binds the
 * view appropriately.
 */
class MessageListAdapter(val context: Context, val thisUser: User, val otherUser: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // values for determining view type
    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    private var messageList = listOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.message_sent_item, parent, false)
            SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.message_received_item, parent, false)
            ReceivedMessageHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageHolder).bind(messageList[position])
        } else {
            (holder as ReceivedMessageHolder).bind(messageList[position])
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderUid == thisUser.userId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }


    inner class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: CircleImageView = itemView.message_sender_civ
        private val name: TextView = itemView.message_sender_name_tv
        private val messageBody: TextView = itemView.message_body_tv
        private val messageTimestamp: TextView = itemView.message_timestamp_tv

        fun bind(message: Message) {
            name.text = otherUser.firstName
            messageBody.text = message.content
            messageTimestamp.text = message.timeStamp.getTimeDisplay()

            val profileImageRef = FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                    .child(otherUser.userId)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(profileImageRef)
                    .into(profileImage)

        }
    }

    inner class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageBody = itemView.sent_message_body_tv
        private val messageTimestamp: TextView = itemView.sent_message_timestamp_tv

        fun bind(message: Message) {
            messageBody.text = message.content
            messageTimestamp.text = message.timeStamp.getTimeDisplay()
        }
    }

    fun setMessages(messageList: List<Message>) {
        this.messageList = messageList
        notifyDataSetChanged()
    }
}