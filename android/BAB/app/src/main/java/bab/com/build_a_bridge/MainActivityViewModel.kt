package bab.com.build_a_bridge

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Regions
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.repositories.RequestRepository
import bab.com.build_a_bridge.utils.FirebaseConversationsLiveDataList
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataList
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataMap
import bab.com.build_a_bridge.utils.RequestListUtil
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
    var feedSkillFilterList = arrayListOf<String>()
    var states: ArrayList<String> = arrayListOf()
    var areas: MutableMap<String, ArrayList<String>> = hashMapOf()

    // Repos
    private val requestRepository: RequestRepository

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

                if(feedSkillFilterList.isEmpty()) feedSkillFilterList.addAll(userSkillsList)
            }
        })

        requestRepository = RequestRepository()
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


    fun refreshRequestList(){
        requestRepository.loadAllRequestsFor(user.state.toString(), user.region.toString())
    }

    fun getRequests(): MutableLiveData<List<Request>>{
        refreshRequestList()
        return requestRepository.getRequests()
    }

    fun filterRequests(allRequests: List<Request>){
       feedFragmentList = RequestListUtil.filterRequests(allRequests, feedSkillFilterList,
               user.userId)
    }

    fun getRegions(){
        FirebaseDatabase.getInstance().reference
                .child("REGIONS")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Regions::class.java)
                        data?.let {
                            for( region in it.locations){
                                states.add(region.state)

                                val areaList = arrayListOf<String>()
                                for( area in region.areas){
                                    areaList.add(area)

                                }
                                areas.put(region.state, areaList)
                            }
                        }
                    }

                })
    }


}