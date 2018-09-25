package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Conversation
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.FirebaseDbRefUtil
import bab.com.build_a_bridge.utils.GlideUtil

import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_request_details.*
import org.jetbrains.anko.toast

/**
 * Shows the two Users that are part of the request and allows actions such as messaging,
 * canceling, and finishing of the request.
 */
class RequestDetailsFragment : Fragment() {
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }
    var requester: User? = null
    var volunteer: User? = null
    var isRequester: Boolean = true

    lateinit var volunteerUserDbRef: DatabaseReference

    companion object {
        private const val DIALOG_FINISH = "dialog_finish"
        private const val REQUEST_FINISH = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getRequesterImage()
        getVolunteerImage()
        getRequester()
        getVolunteer()

        request_title_tv.text = viewModel.requestForDetails.title
        request_details_tv.text = viewModel.requestForDetails.details

        request_details_finish_request_btn.setOnClickListener {
            val mananger = fragmentManager
            val volunteerName = volunteer?.firstName + " " + volunteer?.lastName
            val dialog = FinishRequestFragment.newInstance(volunteerName, volunteer?.userId)
            dialog.setTargetFragment(this, REQUEST_FINISH)
            dialog.show(mananger, DIALOG_FINISH)
        }


        request_details_msg_btn.setOnClickListener { _ ->
            messageButtonClicked()
        }

        setupBottomAppBar()
        setupFinishButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return

        if (requestCode == REQUEST_FINISH) {
            val rating = data?.getFloatExtra(FinishRequestFragment.EXTRA_RATING, 0f)

            // If a rating came through update the volunteer user
            volunteerUserDbRef.runTransaction(object : Transaction.Handler {
                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    Log.i("RequestDetailsFragment", "onComplete: COMPLETE")
                }

                override fun doTransaction(data: MutableData): Transaction.Result {
                    val volunteer = data.getValue(User::class.java)
                    volunteer?.let { nonNullVolunteer ->
                        rating?.let {
                            nonNullVolunteer.ratingTotal += it
                            nonNullVolunteer.numRatings += 1
                            data.value = nonNullVolunteer

                        }
                    }
                    return Transaction.success(data)
                }

            })

            // Update request to COMPLETED status
            val request = viewModel.requestForDetails
            request.status = RequestStatusCodes.COMPLETED

            // Update Request to COMPLETE
            // In Requests by User for requester
            val requesterRequestsDbRef = FirebaseDbRefUtil.getRequestByUserRefWithStatusCode(
                    requester?.userId!!, viewModel.requestForDetails.requestId, RequestStatusCodes.IN_PROGRESS_REQUESTER)


            requesterRequestsDbRef.setValue(request)

            // In request by user for volunteer
            val volunteerRequestsDbRef = FirebaseDbRefUtil.getRequestByUserRefWithStatusCode(
                    volunteer?.userId!!, viewModel.requestForDetails.requestId, RequestStatusCodes.IN_PROGRESS_VOLUNTEER)

            volunteerRequestsDbRef.setValue(request)

            val requestDbRef = FirebaseDbRefUtil.getRequestRef(
                    requester?.state.toString(), requester?.region.toString(), viewModel.requestForDetails.status,
                    viewModel.requestForDetails.requestId)

            requestDbRef.setValue(request)

        }
    }

    private fun messageButtonClicked() {
        val targetForMessage = if (isRequester) volunteer else requester
        targetForMessage?.let { targetUser ->
            viewModel.userToMessage = targetUser

            // Check if conversation already exists
            val msgByUserDbRef =
                    FirebaseDbRefUtil.getMsgByUserTargetUserDbRef(viewModel.user.userId, targetUser.userId)

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
                        FirebaseDbRefUtil.getMsgByUserTargetUserDbRef(targetUser.userId,
                                viewModel.user.userId)
                                .setValue(msgId)

                        // now create a conversation and push that to MESSAGES
                        val newConversation = Conversation(uid1 = viewModel.user.userId,
                                uid2 = targetUser.userId, msgId = msgId!!)

                        FirebaseDbRefUtil.getMsgDbRef(msgId)
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

    private fun getVolunteer() {
        viewModel.requestForDetails.volunteerId?.let { volunteerId ->
            volunteerUserDbRef = FirebaseDbRefUtil.getUserRef(volunteerId)

            volunteerUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    // Do nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    volunteer = dataSnapshot.getValue(User::class.java)
                    volunteer_name_tv.text = volunteer?.firstName
                }
            })
        }
    }

    private fun getRequester() {
        viewModel.requestForDetails.requesterId?.let { requesterId ->
            val requesterUserDbRef = FirebaseDbRefUtil.getUserRef(requesterId)

            requesterUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    // Do nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    requester = dataSnapshot.getValue(User::class.java)
                    requester_name_tv.text = requester?.firstName
                    isRequester = (requester?.userId == viewModel.user.userId)
                }
            })
        }
    }

    private fun getRequesterImage() {
        viewModel.requestForDetails.requesterId?.let { requesterId ->
            GlideUtil.loadProfilePicIntoCiv(request_details_requester_iv, requesterId, context!!)
        }
    }

    private fun getVolunteerImage() {
        viewModel.requestForDetails.volunteerId?.let { volunteerId ->
            GlideUtil.loadProfilePicIntoCiv(request_details_volunteer_iv, volunteerId, context!!)
        }
    }



    private fun setupFinishButton() {
        if (viewModel.requestForDetails.volunteerId == viewModel.user.userId
                || viewModel.requestForDetails.status == RequestStatusCodes.COMPLETED
                || viewModel.requestForDetails.status == RequestStatusCodes.INCOMPLETE) {
            // Current user is volunteer, and can not finish a request
            request_details_finish_request_btn.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(request_details_constraint_layout)
            constraintSet.connect(R.id.request_details_cancel_btn, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8)
            constraintSet.connect(R.id.send_message_btn, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8)
        }
    }

    private fun setupBottomAppBar() {
        activity?.fab?.hide()
    }
}
