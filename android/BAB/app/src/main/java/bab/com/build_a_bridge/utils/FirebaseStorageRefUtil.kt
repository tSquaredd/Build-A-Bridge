package bab.com.build_a_bridge.utils

import bab.com.build_a_bridge.enums.FirebaseStorageNames
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorageRefUtil {
    companion object {
        fun profilePicRef(userId: String): StorageReference {
            return FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                    .child(userId)
        }

        fun skillIconRef(skillId: String): StorageReference {
            return FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.SKILL_ICONS.toString())
                    .child(skillId)
        }
    }
}