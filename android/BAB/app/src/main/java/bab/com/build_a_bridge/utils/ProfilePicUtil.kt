package bab.com.build_a_bridge.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import bab.com.build_a_bridge.enums.PreferenceNames
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ProfilePicUtil {
    companion object {
        const val PHOTO_DIRECTORY = "build_a_bridge"
        const val FILE_NAME = "profile_pic.png"
        fun savePhoto(context: Context, bitmap: Bitmap): String{
            val contextWrapper = ContextWrapper(context)
            val fileDirectory = contextWrapper.getDir(PHOTO_DIRECTORY, Context.MODE_PRIVATE)
            val filePath = File(fileDirectory, FILE_NAME)

            var fileOutputStream: FileOutputStream? = null

            try {
                fileOutputStream = FileOutputStream(filePath)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            } catch (e: Exception){
                e.printStackTrace()
            } finally {
                fileOutputStream?.close()
            }

            return fileDirectory.absolutePath

        }

        fun loadPhotoFromInternalStorage(context: Context): Bitmap?{
            var bitmap: Bitmap? = null
            try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val filePath = prefs.getString(PreferenceNames.PROFILE_PICTURE.toString(), "null")
                val file = File(filePath, FILE_NAME)
                bitmap = BitmapFactory.decodeStream(FileInputStream(file))
            } catch (e: FileNotFoundException){
                e.printStackTrace()
            }

            return bitmap
        }

        fun removePhoto(context: Context){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val filePath = prefs.getString(PreferenceNames.PROFILE_PICTURE.toString(), "null")
            val file = File(filePath, FILE_NAME)
            file.delete()
        }

    }
}