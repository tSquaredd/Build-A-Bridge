package bab.com.build_a_bridge

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataList
import bab.com.build_a_bridge.utils.FirebaseSkillLiveDataMap
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger

/**
 * Holds all data for UI
 */
class MainActivityViewModel(application: Application) : AndroidViewModel(application), AnkoLogger {
    var user: User? = null
    var newRequest: Request? = null
    var requestFeedList: ArrayList<Request> = arrayListOf()
    var requestForDetails: Request = Request()
    val skillLiveDataList: FirebaseSkillLiveDataList
    val skillsLiveDataMap: FirebaseSkillLiveDataMap

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val json = prefs.getString(PreferenceNames.USER.toString(), "")
        user = Gson().fromJson(json, User::class.java)
        val skillDbRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS.toString())
        skillLiveDataList = FirebaseSkillLiveDataList(skillDbRef)
        skillsLiveDataMap = FirebaseSkillLiveDataMap(skillDbRef)
    }

    /**
     * Instantiates a request object in newRequest
     */
    fun initRequest() {
        if (newRequest == null) {
            newRequest = Request()
            newRequest?.requesterId = user?.userId
        }

    }
}