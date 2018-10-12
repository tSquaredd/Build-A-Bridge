package bab.com.build_a_bridge

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bab.com.build_a_bridge.admin.AdminSkillsFragment
import bab.com.build_a_bridge.presentation.*
import bab.com.build_a_bridge.utils.FirebaseStorageRefUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import kotlinx.android.synthetic.main.bottom_sheet.*

class BottomNavDrawerFragment: BottomSheetDialogFragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    companion object {
        const val ARG_CURRENT_SELECTION = "current_selection"
        const val FEED = "feed"
        const val REQUESTS = "requests"
        const val SKILLS = "skills"
        const val MESSAGES = "messages"
        const val FRIENDS = "friends"
        const val SETTINGS = "settings"
        const val PROFILE = "profile"

        const val ADMIN_SKILLS = "admin_skills"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(FirebaseStorageRefUtil.profilePicRef(viewModel.user.userId))
                .into(nav_user_icon_iv)


        val username = "${viewModel.user.firstName} ${viewModel.user.lastName}"
        nav_username_tv.text = username

        nav_user_email_tv.text = viewModel.user.email

        when (arguments?.getString(ARG_CURRENT_SELECTION)){
            FEED -> {
                navigation_view.setCheckedItem(R.id.nav_feed)

            }
            REQUESTS -> {
                navigation_view.setCheckedItem(R.id.nav_requests)
            }
            SKILLS -> {
                navigation_view.setCheckedItem(R.id.nav_skills)
            }
            MESSAGES -> {
                navigation_view.setCheckedItem(R.id.nav_messages)
            }
            FRIENDS -> {
                navigation_view.setCheckedItem(R.id.nav_friends)
            }
            SETTINGS -> {
                navigation_view.setCheckedItem(R.id.nav_settings)
            }
            ADMIN_SKILLS -> {
                navigation_view.setCheckedItem(R.id.nav_admin_skills)
            }
            PROFILE -> {
                navigation_view.setCheckedItem(R.id.nav_profile)
            }
        }


        navigation_view.setNavigationItemSelectedListener {
            val itemId = it.itemId
            val activity = activity as MainActivity
            when (itemId) {
                R.id.nav_feed -> {
                    activity.swapFragments(FeedFragment(), true)

                }
                R.id.nav_requests -> {
                    activity.swapFragments(RequestsFragment(), true)

                }
                R.id.nav_skills -> {
                    activity.swapFragments(SkillsFragment(), true)

                }
                R.id.nav_messages -> {
                    activity.swapFragments(ConversationsFragment(), true)

                }
                R.id.nav_friends -> {
                    activity.swapFragments(FriendsFragment(), true)

                }
                R.id.nav_profile -> {
                    activity.swapFragments(EditProfileFragment(), true)
                }
                R.id.nav_settings -> {
                    activity.swapFragments(SettingsFragment(), true)

                }
                R.id.nav_admin_skills -> {
                    activity.swapFragments(AdminSkillsFragment(), true)

                }
                R.id.nav_sign_out -> {
                    activity.signOut()

                }
            }
            this.dismiss()
            true
        }
    }

    fun setSelected(){
        navigation_view.setCheckedItem(R.id.nav_feed)
    }
}
