package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.text.SpannableString

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.ProfilePicUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_accept_request.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class AcceptRequestFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accept_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // indent first paragraph of Request details
        val spanString = SpannableString(viewModel.requestForAccept.details)
        spanString.setSpan(android.text.style.LeadingMarginSpan.Standard(30, 0), 0, 1, 0)

        request_title_tv.text = viewModel.requestForAccept.title
        request_details_tv.text = spanString

        if (viewModel.requestForAccept.requesterId == viewModel.user.userId) {
            // This is the user viewing their request which has no volunteer.
            val user = viewModel.user
            updateUi(user)
            accept_request_btn.text = getString(R.string.cancel)

            // load profile picture from internal storage
            val picBitmap = ProfilePicUtil.loadPhotoFromInternalStorage(context!!)
            accept_request_requester_iv.setImageBitmap(picBitmap)

        } else {
            // This is the user viewing the Request of another User

            // Get profile image of requester
            val requesterImgRef = FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                    .child(viewModel.requestForAccept.requesterId!!)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(requesterImgRef)
                    .error(R.drawable.default_user_pic)
                    .into(accept_request_requester_iv)


            val requesterDbRef = FirebaseDatabase.getInstance().reference
                    .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                    .child(viewModel.requestForAccept.requesterId!!)

            requesterDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    // Do nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let { updateUi(it) }

                    val buttonText = "${getString(R.string.help)} ${user?.firstName}"
                    accept_request_btn.text = buttonText
                }
            })
        }

        val skillDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS.toString())
                .child(viewModel.requestForAccept.skillId!!)

        skillDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val skill = dataSnapshot.getValue(Skill::class.java)
                skill_name_tv.text = skill?.name

                val skillIconRef = FirebaseStorage.getInstance().reference
                        .child(FirebaseStorageNames.SKILL_ICONS.toString())
                        .child(viewModel.requestForAccept.skillId!!)

                Glide.with(context)
                        .using(FirebaseImageLoader())
                        .load(skillIconRef)
                        .error(R.drawable.ic_default_skill)
                        .into(accept_request_skill_icon)
            }

        })

        accept_request_btn.setOnClickListener {
            // if user is viewing own request enable cancelling, otherwise enable accepting.
            if (viewModel.requestForAccept.requesterId == viewModel.user.userId) cancelRequest()
            else acceptRequest()
        }
    }

    /**
     * User accepts the request and updates database accordingly.
     *
     * Request is removed from that regions REQUESTED status code section and
     * moved into that regions IN_PROGRESS.
     *
     * Within the REQUESTS_BY_USER section of the DB, the Request is removed from the requesters
     * REQUESTED section and added to the requester and volunteer in IN_PROGRESS_REQUESTER
     * and IN_PROGRESS_VOLUNTEER respectively.
     */
    private fun acceptRequest() {

        // update request
        viewModel.requestForAccept.status = RequestStatusCodes.IN_PROGRESS
        viewModel.requestForAccept.volunteerId = viewModel.user?.userId

        // Get reference to requests for the region
        val reqRegionDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(viewModel.user.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(viewModel.user.region.toString())

        // Get reference to DB for IN_PROGRESS requests
        val inProgDbRef = reqRegionDbRef
                .child(RequestStatusCodes.IN_PROGRESS.toString())
                .child(viewModel.requestForAccept.requestId)

        // add request to IN_PROGRESS DB
        inProgDbRef.setValue(viewModel.requestForAccept)

        // Get reference to DB for REQUESTED requests
        val requestedDbRef = reqRegionDbRef
                .child(RequestStatusCodes.REQUESTED.toString())
                .child(viewModel.requestForAccept.requestId)

        // remove request
        requestedDbRef.removeValue()

        // Get DB ref for requesters REQUESTS_BY_USER section
        val requesterRequestsDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                .child(viewModel.requestForAccept.requesterId!!)

        // DB ref for requesters IN_PROGRESS_REQUESTER section
        val requesterInProgDbRef = requesterRequestsDbRef
                .child(RequestStatusCodes.IN_PROGRESS_REQUESTER.toString())
                .child(viewModel.requestForAccept.requestId)

        // set request to this section of DB
        requesterInProgDbRef.setValue(viewModel.requestForAccept)

        // DB ref to requesters REQUESTED section
        val requesterRequestedDbRef = requesterRequestsDbRef
                .child(RequestStatusCodes.REQUESTED.toString())
                .child(viewModel.requestForAccept.requestId)

        // remove the request
        requesterRequestedDbRef.removeValue()

        // DB ref to volunteers IN_PROGRESS_VOLUNTEER section
        val volunteerInProgDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                .child(viewModel.user.userId)
                .child(RequestStatusCodes.IN_PROGRESS_VOLUNTEER.toString())
                .child(viewModel.requestForAccept.requestId)

        // add the request
        volunteerInProgDbRef.setValue(viewModel.requestForAccept)

        // Set Request object in view model for use in RequestDetailsFragment
        viewModel.requestForDetails = viewModel.requestForAccept
        val mainActivity = activity as MainActivity
        mainActivity.swapFragments(RequestDetailsFragment(), false)

    }

    private fun cancelRequest() {
        activity?.toast("COMING SOON :)")
        // TODO: Add cancelling functionality
    }

    fun updateUi(user: User) {
        val displayName = "${user.firstName} ${user.lastName}"
        requester_name_tv.text = displayName
    }
}
