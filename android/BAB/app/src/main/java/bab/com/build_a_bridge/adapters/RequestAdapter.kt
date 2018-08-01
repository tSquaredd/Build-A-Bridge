package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Request
import bab.com.build_a_bridge.utils.FirebaseRequestHandler
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.request_list_item.view.*

class RequestAdapter(val requestList: ArrayList<Request>, val context: Context): RecyclerView.Adapter<RequestAdapter.RequestHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RequestHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.request_list_item, parent, false)
        return RequestHolder(view)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {
        holder.requestTitle.text = requestList[position].requestTitle
        holder.requestDetails.text = requestList[position].requestDetails

        val storageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                .child(requestList[position].requester?.userId!!)

        storageRef.downloadUrl.addOnSuccessListener {
            val picassoInstance = Picasso.Builder(context)
                    .addRequestHandler(FirebaseRequestHandler()).build()

            // TODO: Image loading is slow here
            picassoInstance.load(it).into(holder.requesterImage)
        }
    }


    inner class RequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val requestTitle: TextView = itemView.request_item_title
        val requestDetails: TextView = itemView.request_item_details
        val requesterImage: CircleImageView = itemView.requester_profile_img

        init {
            itemView.setOnClickListener {
                // TODO: add click listener
            }
        }
    }
}