package bab.com.build_a_bridge.objects

import bab.com.build_a_bridge.enums.RequestStatusCodes
import java.util.*

/**
 * Models a request for help from one user to others.
 * Once accepted by a volunteer is a form of contract between the
 * two parties.
 */
data class Request(var requestId: String = "",
                   var requesterId: String? = null,
                   var volunteerId: String? = null,
                   var status: RequestStatusCodes = RequestStatusCodes.REQUESTED,
                   var skillId: String? = null,
                   var title: String = "",
                   var details: String = "",
                   val timeStamp: TimeStamp = TimeStamp.getInstance())