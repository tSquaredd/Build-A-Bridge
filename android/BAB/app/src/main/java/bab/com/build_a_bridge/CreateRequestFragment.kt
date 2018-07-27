package bab.com.build_a_bridge


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import com.firebase.ui.auth.viewmodel.RequestCodes
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_create_request.*


class CreateRequestFragment :  Fragment(){

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_request_button.setOnClickListener {
            testCreation()
        }
    }

    fun testCreation(){


        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(viewModel.user?.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(viewModel.user?.region.toString())
                .child(RequestStatusCodes.REQUESTED.toString())

        val reqId = db.push().key

        val request = Request(reqId.toString(), viewModel.user, null, RequestStatusCodes.REQUESTED, Skill("7", "Translation", "Help as a translator"),
                "Need help reading newspaper", "I can not understand this darn newspaper, and I want to be " +
                "able to keep up on current events, and learn english. Please help!")

        db.setValue(request)

    }
}
