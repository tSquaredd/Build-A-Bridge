package bab.com.build_a_bridge.objects

import bab.com.build_a_bridge.enums.RequestStatusCodes
import java.util.*

class Request(var requestId: String = "",
              var requester: User? = null,
              var volunteer: User? = null,
              var status: RequestStatusCodes = RequestStatusCodes.REQUESTED,
              var skillRequested: Skill? = null,
              var requestTitle: String = "",
              var requestDetails: String = "",
              val timeStamp: TimeStamp = TimeStamp.getInstance())