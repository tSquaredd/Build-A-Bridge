package bab.com.build_a_bridge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Models a Skill that a User can have.
 */
@Parcelize
data class Skill(var id: String = "", var name: String = "", var description: String = ""): Parcelable