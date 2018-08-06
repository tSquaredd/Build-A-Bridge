package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.MainActivity
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.AcceptRequestFragment
import bab.com.build_a_bridge.enums.FirebaseDbNames
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.objects.User
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.request_list_item.view.*

class RequestAdapter(val requestList: ArrayList<Request>, val context: Context, val activity: MainActivity): RecyclerView.Adapter<RequestAdapter.RequestHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RequestHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.request_list_item, parent, false)
        return RequestHolder(view)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {
        holder.requestTitle.text = requestList[position].title
        val userDb = FirebaseDatabase.getInstance().reference
                .child(FirebaseDbNames.USER_ID_DIRECTORY.toString())
                .child(requestList[position].requesterId!!)

        userDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                // Do nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val displayName = "${user?.firstName} ${user?.lastName}"
                holder.requesterName.text = displayName
            }

        })

        val storageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.SKILL_ICONS.toString())
                .child(requestList[position].skillId!!)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(storageRef)
                    .into(holder.requestSkillIcon)

    }


    inner class RequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val requestTitle: TextView = itemView.request_item_title
        val requesterName: TextView = itemView.requester_name_tv
        val requestSkillIcon: AppCompatImageView = itemView.request_skill_category_iv

        init {
            itemView.setOnClickListener {
                activity.viewModel.requestForDetails = requestList[adapterPosition]
                activity.swapFragments(AcceptRequestFragment())
            }
        }
    }
}