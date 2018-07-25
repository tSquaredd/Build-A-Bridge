package bab.com.build_a_bridge.objects

import android.graphics.Region
import bab.com.build_a_bridge.enums.RegionCodes

class User(var firstName: String = "", var lastName: String = "", var phoneNumber: String? = null,
           var state: RegionCodes = RegionCodes.DEFAULT, var region: RegionCodes = RegionCodes.DEFAULT, var rating: Double? = null,
           var email: String = "", val userId: String = "")