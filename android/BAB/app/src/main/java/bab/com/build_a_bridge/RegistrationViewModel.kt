package bab.com.build_a_bridge

import android.arch.lifecycle.ViewModel
import bab.com.build_a_bridge.enums.RegionCodes

class RegistrationViewModel: ViewModel() {
    var firstName: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var state: RegionCodes? = null
    var region: RegionCodes? = null
}