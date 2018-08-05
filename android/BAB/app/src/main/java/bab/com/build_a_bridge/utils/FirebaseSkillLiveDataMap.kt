package bab.com.build_a_bridge.utils

import android.arch.lifecycle.LiveData
import bab.com.build_a_bridge.objects.Skill
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Extension of live data class to observe changes to skills in DB and put those skills
 * into a map
 */
class FirebaseSkillLiveDataMap(val databaseReference: DatabaseReference) : LiveData<MutableMap<String, Skill>>(), AnkoLogger {

    private val listener: SkillValueEventListener

    init {
        listener = SkillValueEventListener()
        onActive()
    }

    override fun onActive() {
        databaseReference.addValueEventListener(listener)
    }

    override fun onInactive() {
        databaseReference.removeEventListener(listener)
    }


    private inner class SkillValueEventListener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            // Do nothing
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val skillMap = mutableMapOf<String, Skill>()
            val dataChildren = dataSnapshot.children
            for (skillChild in dataChildren) {
                val skill = skillChild.getValue(Skill::class.java)
                skill?.let { skillMap.put(it.id, it) }
            }

            value = skillMap


        }

    }
}
