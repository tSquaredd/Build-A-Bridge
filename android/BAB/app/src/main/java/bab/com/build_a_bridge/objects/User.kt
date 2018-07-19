package bab.com.build_a_bridge.objects

import android.graphics.Region
import bab.com.build_a_bridge.enums.RegionCodes

class User(val firstName: String, val lastName: String, var phoneNumber: String?,
           var state: RegionCodes, var region: RegionCodes, var rating: Double?,
           var email: String?)