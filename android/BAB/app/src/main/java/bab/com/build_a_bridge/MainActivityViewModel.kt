package bab.com.build_a_bridge

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.FirebaseConversationsLiveDataList
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataList
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Holds all data for UI
 */
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var user: User = User()
    var newRequest: Request? = null
    var feedFragmentList: ArrayList<Request> = arrayListOf()
    var requestsFragmentList: ArrayList<Request> = arrayListOf()
    var requestForAccept: Request = Request()
    var requestForDetails: Request = Request()
    val skillLiveDataList: FirebaseSkillLiveDataList
    val skillsLiveDataMap: FirebaseSkillLiveDataMap
    val conversationsLiveData: FirebaseConversationsLiveDataList
    var userSkillsList: ArrayList<String> = arrayListOf()
    private val userSkillListener: ValueEventListener
    private val userSkillDbRef: DatabaseReference
    var userToMessage: User = User()
    var messageId: String = ""
    var feedSkillFilterList = arrayListOf<Skill>()

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val json = prefs.getString(PreferenceNames.USER.toString(), "")
        user = Gson().fromJson(json, User::class.java)

        skillLiveDataList = FirebaseSkillLiveDataList()
        skillsLiveDataMap = FirebaseSkillLiveDataMap()

        conversationsLiveData = FirebaseConversationsLiveDataList()

        userSkillDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS_BY_USER.toString())
                .child(FirebaseAuth.getInstance().uid!!)

        userSkillListener = userSkillDbRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userSkillsList = arrayListOf()
                val children = dataSnapshot.children
                for (child in children) {
                    userSkillsList.add(child.key.toString())

                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        userSkillDbRef.removeEventListener(userSkillListener)
    }

    /**
     * Instantiates a request object in newRequest
     */
    fun initRequest() {
        if (newRequest == null) {
            newRequest = Request()
            newRequest?.requesterId = user.userId
        }

    }

}