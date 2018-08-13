package bab.com.build_a_bridge.objects

import bab.com.build_a_bridge.enums.RegionCodes

/**
 * Models data for a user of the platform
 */
class User(var firstName: String = "",
           var lastName: String = "", 
           var phoneNumber: String? = null,
           var state: RegionCodes = RegionCodes.DEFAULT,
           var region: RegionCodes = RegionCodes.DEFAULT,
           var rating: Double? = null,
           var email: String = "",
           val userId: String = "",
           var fcmToken: String = "")