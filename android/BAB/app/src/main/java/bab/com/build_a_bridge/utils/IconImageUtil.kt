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

/**
 * Util for saving / loading / removing images for Skill icons from internal storage.
 *
 * TODO: this is not being used. If we decide to save skill icons internally this would be useful
 */
class IconImageUtil {
    companion object {
        const val PHOTO_DIRECTORY = "build_a_bridge_skill_icons"

        fun savePhoto(context: Context, bitmap: Bitmap, skillId: String): String{
            val contextWrapper = ContextWrapper(context)
            val fileDirectory = contextWrapper.getDir(PHOTO_DIRECTORY, Context.MODE_PRIVATE)
            val filePath = File(fileDirectory, skillId)

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

        fun loadPhotoFromInternalStorage(context: Context, skillId: String): Bitmap?{
            var bitmap: Bitmap? = null
            try {
                val directory = context.filesDir
                val file = File(directory, skillId)
                bitmap = BitmapFactory.decodeStream(FileInputStream(file))
            } catch (e: FileNotFoundException){
                e.printStackTrace()
            }

            return bitmap
        }

        fun removePhoto(context: Context, skillId: String){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val filePath = prefs.getString(PreferenceNames.PROFILE_PICTURE.toString(), "null")
            val file = File(filePath, skillId)
            file.delete()
        }
    }
}