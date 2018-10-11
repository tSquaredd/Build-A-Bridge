package bab.com.build_a_bridge.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.utils.FirebaseStorageRefUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import kotlinx.android.synthetic.main.fragment_registration_user_info.*


class EditProfileFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel.getRegions()
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

        spinnerSetup()
    }


    private fun spinnerSetup() {
        val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                viewModel.states)
        state_spinner.adapter = adapter
        state_spinner.setTitle(getString(R.string.select_state))
        region_spinner.setTitle(getString(R.string.select_region))


        region_spinner.isEnabled = false
        state_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, selecetedItemView: View?, position: Int, p3: Long) {
                viewModel.user.state = viewModel.states[position]

                val regions = viewModel.areas[viewModel.user.state]

                val regionAdapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                        regions)

                region_spinner.adapter = regionAdapter
                region_spinner.isEnabled = true
            }
        }

        region_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.areas[viewModel.user.state]?.let {
                    viewModel.user.region = it[position]
                }


            }
        }


    }
}
