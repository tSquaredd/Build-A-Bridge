package bab.com.build_a_bridge.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import bab.com.build_a_bridge.R
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import de.hdodenhof.circleimageview.CircleImageView

class GlideUtil {

    companion object {
        fun loadProfilePicIntoCiv(view: CircleImageView, userId: String, context: Context){
            val imgRef = FirebaseStorageRefUtil.profilePicRef(userId)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(imgRef)
                    .error(R.drawable.default_user_pic)
                    .into(view)
        }

        fun loadSkillIconIntoIv(view: ImageView, skillId: String, context: Context){
            val imgRef = FirebaseStorageRefUtil.skillIconRef(skillId)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(imgRef)
                    .error(R.drawable.ic_default_skill)
                    .into(view)
        }
    }


}