package bab.com.build_a_bridge

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.enums.RegionCodes
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var user: User? = null
    var systemSkillsList: ArrayList<Skill> = arrayListOf()
    var newRequest: Request? = null
    var requestFeedList: ArrayList<Request> = arrayListOf()

    /**
     * Initializes the ViewModel
     */
    fun init() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val json = prefs.getString(PreferenceNames.USER.toString(), "")
        user = Gson().fromJson(json, User::class.java)

        getSystemSkillsList()
        getRequestFeedList()
    }


    /**
     * Gets list of skills from firebase DB
     */
    private fun getSystemSkillsList() {
        // empty the list
        systemSkillsList = arrayListOf()

        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS.toString())

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            // add skill instance to skills list
            override fun onDataChange(data: DataSnapshot) {
                val skills = data.children
                for (skill: DataSnapshot in skills) {
                    val skillInstance = skill.getValue(Skill::class.java)
                    skillInstance?.let { systemSkillsList.add(skillInstance) }
                }
            }
        })
    }

    /**
     * Instantiates a request object in newRequest
     */
    fun initRequest() {
        if (newRequest == null) {
            newRequest = Request()
            newRequest?.requester = user
        }

    }

    /**
     * Gets all requests with status REQUESTED in users state/region that are not
     * requests of the user themselves
     */
    private fun getRequestFeedList() {
        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.REQUESTS.toString())
                .child(FirebaseDbNames.STATE.toString())
                .child(user?.state.toString())
                .child(FirebaseDbNames.REGION.toString())
                .child(user?.region.toString())
                .child(RequestStatusCodes.REQUESTED.toString())

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val requests = p0.children
                for (d: DataSnapshot in requests) {
                    val request = d.getValue(Request::class.java)
                    request?.let {
                        if (it.requestId != user?.userId)
                            requestFeedList.add(it)
                    }
                }
            }

        })
    }
}