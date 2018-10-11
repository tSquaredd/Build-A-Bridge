package bab.com.build_a_bridge

import android.arch.lifecycle.ViewModel
import bab.com.build_a_bridge.objects.Regions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistrationViewModel : ViewModel() {
    var firstName: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var state: String? = null
    var region: String? = null

    var states: ArrayList<String> = arrayListOf()
    var areas: MutableMap<String, ArrayList<String>> = hashMapOf()




    init {
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