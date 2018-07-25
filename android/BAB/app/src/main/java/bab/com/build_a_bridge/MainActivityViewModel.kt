package bab.com.build_a_bridge

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.PreferenceNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.objects.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivityViewModel(application: Application) : AndroidViewModel(application), AnkoLogger {
    var user: User? = null
    var systemSkillsList: ArrayList<Skill> = arrayListOf()

    fun init(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val json = prefs.getString(PreferenceNames.USER.toString(), "")
        user = Gson().fromJson(json, User::class.java)
        info(user.toString())

        getSystemSkillsList()
    }

    fun getSystemSkillsList(){
        // empty the list
        systemSkillsList = arrayListOf()

        val db = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.SKILLS.toString())

        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(data: DataSnapshot) {
               val skills = data.children
                for(skill: DataSnapshot in skills){
                    val skillInstance = skill.getValue(Skill::class.java)
                    skillInstance?.let { systemSkillsList.add(skillInstance) }
                }

                for(skill in systemSkillsList)
                    info("Skills list is " + skill.toString())
            }

        })
    }
}