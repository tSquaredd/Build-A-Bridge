package bab.com.build_a_bridge.presentation



import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.utils.FirebaseStorageRefUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import kotlinx.android.synthetic.main.fragment_registration_user_info.*


class EditProfileFragment : Fragment(){

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(FirebaseStorageRefUtil.profilePicRef(viewModel.user.userId))
                .into(profile_pic_circle_image_view)

        first_name_text_input_edit_text.setText(viewModel.user.firstName, TextView.BufferType.EDITABLE)
        last_name_text_input_edit_text.setText(viewModel.user.lastName, TextView.BufferType.EDITABLE)
        phone_number_text_input_edit_text.setText(viewModel.user.phoneNumber, TextView.BufferType.EDITABLE)
        next_button.visibility = View.GONE
    }


}
