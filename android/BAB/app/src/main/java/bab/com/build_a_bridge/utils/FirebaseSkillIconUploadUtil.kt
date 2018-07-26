package bab.com.build_a_bridge.utils

import android.net.Uri
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import com.google.firebase.storage.FirebaseStorage

abstract class FirebaseSkillIconUploadUtil {
    abstract fun onPhotUploadSuccess()
    abstract fun onPhotoUploadFailure()
    abstract fun photoUploadProgress(progress: Double)

    fun uploadPhoto(filePath: Uri, skillId: String){
        val storageRef =
                FirebaseStorage.getInstance().reference
                        .child(FirebaseStorageNames.SKILL_ICONS.toString())
                        .child(skillId)

        storageRef.putFile(filePath)
                .addOnSuccessListener { onPhotUploadSuccess() }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                    photoUploadProgress(progress)
                }
                .addOnFailureListener { onPhotoUploadFailure() }

    }
}