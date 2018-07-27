package bab.com.build_a_bridge.objects

import bab.com.build_a_bridge.enums.RequestStatusCodes
import java.util.*

class Request(val requestId: String,
              val requester: User? = null, var volunteer: User? = null,
              var status: RequestStatusCodes = RequestStatusCodes.REQUESTED,
              var skillRequested: Skill? = null,
              var requestTitle: String = "",
              var requestDetails: String = "",
              val dateRequested: Date = Calendar.getInstance().time)