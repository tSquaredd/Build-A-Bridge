package bab.com.build_a_bridge.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.User
import com.bumptech.glide.Glide

import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_request_details.*
import org.jetbrains.anko.toast

/**
 * Shows the two Users that are part of the request and allows actions such as messaging and
 * canceling of the request.
 */
class RequestDetailsFragment : Fragment() {
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    var requester: User? = null
    var volunteer: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get image for requester
        val requesterImageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                .child(viewModel.requestForDetails.requesterId!!)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(requesterImageRef)
                .error(R.drawable.default_user_pic)
                .into(request_details_requester_iv)

        // get image for volunteer
        val volunteerImageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                .child(viewModel.requestForDetails.volunteerId!!)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(volunteerImageRef)
                .error(R.drawable.default_user_pic)
                .into(request_details_volunteer_iv)

        // Get requester details
        val requesterUserDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(viewModel.requestForDetails.requesterId!!)

        requesterUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                requester = dataSnapshot.getValue(User::class.java)
                requester_name_tv.text = requester?.firstName
            }
        })


        // get volunteer details
        val volunteerUserDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(viewModel.requestForDetails.volunteerId!!)

        volunteerUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                volunteer = dataSnapshot.getValue(User::class.java)
                volunteer_name_tv.text = volunteer?.firstName
            }
        })

        request_title_tv.text = viewModel.requestForDetails.title
        request_details_tv.text = viewModel.requestForDetails.details


        request_details_msg_btn.setOnClickListener { _ ->
            val isRequester = (requester?.userId == viewModel.user.userId)
            val targetForMessage = if (isRequester) volunteer else requester
            targetForMessage?.let { targetUser ->
                viewModel.userToMessage = targetUser

                // Check if conversation already exists
                val msgByUserDbRef = FirebaseDatabase.getInstance().reference
                        .child(FirebaseDbNames.MESSAGES_BY_USER.toString())
                        .child(viewModel.user.userId)
                        .child(targetUser.userId)

                msgByUserDbRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Do nothing
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val msgId: String? = dataSnapshot.getValue(String::class.java)
                            msgId?.let {
                                viewModel.messageId = it
                                (activity as MainActivity).swapFragments(MessagingFragment(), true)
                            }
                        } else {
                            // no message ID so this is new message
                            val msgId = msgByUserDbRef.push().key

                            // puts msgId into this users MESSAGE_BY_USER_DB
                            msgByUserDbRef.setValue(msgId)

                            // now set on other users
                            FirebaseDatabase.getInstance().reference
                                    .child(FirebaseDbNames.MESSAGES_BY_USER.toString())
                                    .child(targetUser.userId)
                                    .child(viewModel.user.userId)
                                    .setValue(msgId)

                            // now create a conversation and push that to MESSAGES
                            val newConversation = Conversation(uid1 = viewModel.user.userId,
                                    uid2 = targetUser.userId, msgId = msgId!!)

                            FirebaseDatabase.getInstance().reference
                                    .child(FirebaseDbNames.MESSAGES.toString())
                                    .child(msgId)
                                    .setValue(newConversation)
                                    .addOnCompleteListener {
                                        (activity as MainActivity).swapFragments(MessagingFragment(), true)
                                    }
                                    .addOnFailureListener {
                                        activity?.toast(getString(R.string.failed_creating_conversation))
                                    }
                        }
                    }
                })
            }
        }

        setupBottomAppBar()
    }

    private fun setupBottomAppBar(){
        activity?.fab?.hide()
    }
}
