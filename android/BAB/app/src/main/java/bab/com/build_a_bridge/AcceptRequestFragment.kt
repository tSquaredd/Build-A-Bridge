package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_accept_request.*
import org.jetbrains.anko.AnkoLogger

class AcceptRequestFragment : Fragment(), AnkoLogger {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accept_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        request_title_tv.text = viewModel.requestForDetails.title
        request_details_tv.text = viewModel.requestForDetails.details

        accept_request_btn.setOnClickListener {  }
    }


}
