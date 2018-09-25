package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.enums.RequestStatusCodes
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.User
import bab.com.build_a_bridge.presentation.MainActivity
import bab.com.build_a_bridge.utils.GlideUtil
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.active_request_list_item.view.*
import kotlinx.android.synthetic.main.request_list_item.view.*

abstract class ActiveRequestAdapter(var requestList: ArrayList<Request>, val userId: String, val context: Context, val activity: MainActivity) : RecyclerView.Adapter<ActiveRequestAdapter.ActiveRequestHolder>() {

    abstract fun onItemClick(position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ActiveRequestHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.active_request_list_item, parent, false)
        return ActiveRequestHolder(view)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ActiveRequestHolder, position: Int) {
        val currentRequest = requestList[position]
        holder.requestTitle.text = currentRequest.title

        if (currentRequest.requesterId == userId)
            holder.userRole.text = context.getString(R.string.role_label, context.getString(R.string.requester))
        else
            holder.userRole.text = context.getString(R.string.role_label, context.getString(R.string.volunteer))

        holder.status.text = when(currentRequest.status){
            RequestStatusCodes.REQUESTED -> {
                context.getString(R.string.status_label, context.getString(R.string.requested))
            }
            RequestStatusCodes.IN_PROGRESS_REQUESTER -> {
                context.getString(R.string.status_label, context.getString(R.string.in_progress))
            }
            RequestStatusCodes.IN_PROGRESS_VOLUNTEER -> {
                context.getString(R.string.status_label, context.getString(R.string.in_progress))
            }
            RequestStatusCodes.IN_PROGRESS -> {
                // This status code should never appear in this DB path but must be included to
                // be exhaustive
                context.getString(R.string.status_label, context.getString(R.string.in_progress))
            }
            RequestStatusCodes.COMPLETED -> {
                context.getString(R.string.status_label, context.getString(R.string.completed))
            }
            RequestStatusCodes.INCOMPLETE -> {
                context.getString(R.string.status_label, context.getString(R.string.incomplete))
            }
        }


        var userDb = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())

        if (currentRequest.requesterId == userId) {
            if (currentRequest.volunteerId != null) {
                userDb = userDb.child(currentRequest.volunteerId!!)

            } else {
                return
            }
        } else {
            userDb = userDb.child(currentRequest.requesterId!!)

        }
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val displayName = "${user?.firstName} ${user?.lastName}"
                holder.requesterName.text = displayName
            }

        })

        currentRequest.skillId?.let { skillId ->
            GlideUtil.loadSkillIconIntoIv(holder.requestSkillIcon, skillId, context)
        }

        currentRequest.requesterId?.let { requesterId ->
            GlideUtil.loadProfilePicIntoCiv(holder.otherUserImage, requesterId, context )
        }
    }

    fun updateRequestList(requestList: ArrayList<Request>) {
        this.requestList = requestList
        notifyDataSetChanged()
    }


    inner class ActiveRequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val requestTitle: TextView = itemView.active_request_item_title_tv
        val requesterName: TextView = itemView.active_requester_name_tv
        val userRole: TextView = itemView.active_role_tv
        val status: TextView = itemView.active_status_tv
        val requestSkillIcon: AppCompatImageView = itemView.active_request_skill_category_iv
        val otherUserImage: CircleImageView = itemView.active_request_user_image_iv

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }
}