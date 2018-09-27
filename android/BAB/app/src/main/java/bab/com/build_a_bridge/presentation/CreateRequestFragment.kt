package bab.com.build_a_bridge.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.MainActivityViewModel
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.GlideUtil
import bab.com.build_a_bridge.utils.ValidationUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_create_request.*
import kotlinx.android.synthetic.main.skill_list_item.view.*


/**
 * Fragment to guide user through creation of a request
 */
class CreateRequestFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    companion object {
        const val BAR_STYLE = "create_request"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomAppBar()

        // init new request object in View model
        viewModel.initRequest()

        skill_selection_item.setOnClickListener {
            val activity = activity as MainActivity
            activity.swapFragments(SkillSelectionFragment(), true)
        }

        request_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val string = p0.toString().trim()
                if (string.length <= 40)
                    viewModel.newRequest?.title = string
                else {
                    request_title.error = getString(R.string.request_title_length_error)
                    viewModel.newRequest?.title = ""
                }

                isRequestReady()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

        })

        request_details.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val string = p0.toString().trim()
                if (string.length > 2000) {
                    request_details.error = getString(R.string.request_details_length_error)
                    viewModel.newRequest?.details = ""
                } else
                    viewModel.newRequest?.details = string

                isRequestReady()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }
        })
    }

    private fun setupBottomAppBar() {
        activity?.fab?.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                activity?.bottom_app_bar?.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                activity?.fab?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_send))

                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        activity?.fab?.show()
                    }

                }, 200)
            }
        })

    }


    override fun onResume() {
        super.onResume()
        viewModel.skillsLiveDataMap.value?.get(viewModel.newRequest?.skillId)?.let {
            updateSkillUi(it)
        }
        isRequestReady()
    }

    /**
     * Used to check if request is valid, and ready to be submitted
     */
    fun isRequestReady(): Boolean {
        return (viewModel.newRequest?.title != "" && viewModel.newRequest?.details != null
                && viewModel.newRequest?.skillId != null)
    }


    /**
     * Called on return from SkillSelectionFragment in order to update the skill item that
     * was selected
     */
    private fun updateSkillUi(skill: Skill) {
        skill_selection_item.skill_name.text = skill.name
        skill_selection_item.skill_description.text = skill.description

        GlideUtil.loadSkillIconIntoIv(skill_selection_item.skill_icon, skill.id, context!!)

    }

    /**
     * Pushes the new request to the firebase DB
     */
    fun requestCreation() {

        // add request to REQUESTS on firabse DB
        var db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(viewModel.user.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(viewModel.user.region.toString())
                .child(RequestStatusCodes.REQUESTED.toString())

        // get unique id
        val reqId = db.push().key
        reqId?.let { viewModel.newRequest?.requestId = it }

        db = db.child(reqId.toString())

        db.setValue(viewModel.newRequest)

        // add request to REQUESTS_BY_USER on firebase DB
        val db2 = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS_BY_USER.toString())
                .child(viewModel.user.userId)
                .child(RequestStatusCodes.REQUESTED.toString())
                .child(reqId.toString())

        db2.setValue(viewModel.newRequest)

        activity?.onBackPressed()

    }
}
