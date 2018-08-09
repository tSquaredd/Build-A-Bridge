package bab.com.build_a_bridge.utils

import android.arch.lifecycle.LiveData
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.objects.Skill
import com.google.firebase.database.*


/**
 * Extension of the LiveData class for observing the Skill's on the FirebaseDb
 */
class FirebaseSkillLiveDataList : LiveData<List<Skill>>() {

    private val listener: SkillValueEventListener
    private val databaseReference = FirebaseDatabase.getInstance().reference
            .child(FirebaseDbNames.SKILLS.toString())

    init {
        listener = SkillValueEventListener()
    }
    override fun onActive() {
        databaseReference.addValueEventListener(listener)
    }

    override fun onInactive() {
        databaseReference.removeEventListener(listener)
    }


    private inner class SkillValueEventListener : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            // Do nothing
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val skillList = arrayListOf<Skill>()
            val dataChildren = dataSnapshot.children
            for(skillChild in dataChildren){
                val skill = skillChild.getValue(Skill::class.java)
                skill?.let { skillList.add(it) }
            }

            value = skillList
        }
    }
}