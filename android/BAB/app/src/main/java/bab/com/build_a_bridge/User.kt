package bab.com.build_a_bridge

import com.firebase.ui.auth.data.model.PhoneNumber

class User(val firstName: String,
           val lastName: String,
           val phoneNumber: String?,
           val location: String?,
           var requesterRating: Double?,
           var volunteerRating: Double?,
           var email: String
           ) {
}