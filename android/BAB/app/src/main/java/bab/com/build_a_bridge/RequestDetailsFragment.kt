package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.User
import com.bumptech.glide.Glide

import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_request_details.*


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
                .into(request_details_requester_iv)

        // get image for volunteer
        val volunteerImageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                .child(viewModel.requestForDetails.volunteerId!!)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(volunteerImageRef)
                .into(request_details_volunteer_iv)

        // Get requester details
        val requesterUserDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(viewModel.requestForDetails.requesterId!!)

        requesterUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener{
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

        volunteerUserDbRef.addListenerForSingleValueEvent(object : ValueEventListener{
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

    }


}
