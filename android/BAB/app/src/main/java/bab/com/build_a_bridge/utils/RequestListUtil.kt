package bab.com.build_a_bridge.utils

import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.Skill

class RequestListUtil {

    companion object {

        fun filterRequests(allRequests: List<Request>, filterList: List<String>, userId: String): ArrayList<Request>{
            val requestList = arrayListOf<Request>()
            requestList.clear()
            for(request in allRequests){
                if(request.requesterId != userId){
                    if(filterList.isEmpty()){
                        requestList.add(request)
                    } else {
                        for(i in 0 until filterList.size){
                            if(request.skillId == filterList[i]){
                                requestList.add(request)
                                break
                            }
                        }
                    }
                }
            }

            return requestList
        }
    }
}