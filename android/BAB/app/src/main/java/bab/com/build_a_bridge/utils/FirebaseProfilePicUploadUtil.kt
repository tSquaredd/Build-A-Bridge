package bab.com.build_a_bridge.utils

import android.content.Context
import android.net.Uri
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

abstract class FirebaseProfilePicUploadUtil {
    abstract fun onPhotoUploadSuccess()
    abstract fun onPhotoUploadFailure()
    abstract fun photoUploadProgress(progress: Double)

    fun uploadPhoto(filePath: Uri, context: Context){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
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