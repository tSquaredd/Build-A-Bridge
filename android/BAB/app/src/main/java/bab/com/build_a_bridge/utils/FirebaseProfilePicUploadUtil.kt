package bab.com.build_a_bridge.utils


import android.net.Uri
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


/**
 * Uploads a profile picture to Firebase Storage
 */
abstract class FirebaseProfilePicUploadUtil {
    abstract fun onPhotoUploadSuccess()
    abstract fun onPhotoUploadFailure()
    abstract fun photoUploadProgress(progress: Double)

    fun uploadPhoto(filePath: Uri) {
        val storageRef =
                FirebaseStorage.getInstance().reference
                        .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                        .child(FirebaseAuth.getInstance().uid!!)


        storageRef.putFile(filePath)
                .addOnSuccessListener { onPhotoUploadSuccess() }
                .addOnFailureListener { onPhotoUploadFailure() }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                    photoUploadProgress(progress)
                }

    }
}