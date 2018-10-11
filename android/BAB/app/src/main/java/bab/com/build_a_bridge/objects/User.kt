package bab.com.build_a_bridge.objects

import bab.com.build_a_bridge.enums.RegionCodes

/**
 * Models data for a user of the platform
 */
class User(var firstName: String = "",
           var lastName: String = "", 
           var phoneNumber: String? = null,
           var state: String = "",
           var region: String ="",
           var email: String = "",
           val userId: String = "",
           var fcmToken: String = "",
           var numRatings: Int = 0,
           var ratingTotal: Double = 0.0)