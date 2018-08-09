package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.MessagingFragment
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.User
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.conversation_item_layout.view.*

/**
 * Adapter for a list of Conversation objects.
 */
class ConversationListAdapter(val context: Context, var activity: MainActivity) : RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder>() {

    var conversationList = listOf<Conversation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_item_layout, parent, false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversationList[position]

        // other user being the user in the conversation that is not the operating user
        // determine which user is the current user based on uid
        val otherUserUid =
                if (conversation.uid1 == FirebaseAuth.getInstance().uid)
                    conversation.uid2
                else
                    conversation.uid1

        val otherUserDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(otherUserUid)

        otherUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val otherUser = dataSnapshot.getValue(User::class.java)
                holder.name.text = otherUser?.firstName
            }

        })

        holder.lastMessage.text = conversation.messageHistory[conversation.messageHistory.size - 1].content


        // Get other users profile picture
        val profilePicDbRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                .child(otherUserUid)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(profilePicDbRef)
                .error(R.drawable.default_user_pic)
                .into(holder.userProfilePic)
    }

    /**
     * ViewHolder for Conversation objects.
     * {@
     */
    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.conversation_user_name_tv
        val lastMessage: TextView = itemView.conversation_last_message_tv
        val userProfilePic: CircleImageView = itemView.conversation_profile_pic_civ

        init {
            itemView.setOnClickListener {
                val userToMessageUid =
                        if (conversationList[adapterPosition].uid1 == activity.viewModel.user.userId) {
                            conversationList[adapterPosition].uid2
                        } else {
                            conversationList[adapterPosition].uid1
                        }

                val userDbRef = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                        .child(userToMessageUid)

                userDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Do nothing
                    }

                    /**
                     * // Get the user data and add it to MainActivityViewModel for the MessagingFragment to use
                     */
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userToMessage = dataSnapshot.getValue(User::class.java)
                        userToMessage?.let { nonNullUser ->
                            activity.viewModel.userToMessage = nonNullUser
                            activity.viewModel.messageId = conversationList[adapterPosition].msgId
                            activity.swapFragments(MessagingFragment(), true)

                        }
                    }
                })
            }
        }
    }

    /**
     * When the LiveData object that watches the dataset from Firebase observes a change,
     * it uses this method to set the new data and refresh the list view.
     */
    fun setConversations(conversationList: List<Conversation>) {
        this.conversationList = conversationList.sortedBy {
            it.lastUpdated
        }
        notifyDataSetChanged()
    }
}