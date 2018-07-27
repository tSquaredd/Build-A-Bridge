package bab.com.build_a_bridge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Skill(var id: String = "", var name: String = "", var description: String = ""): Parcelable