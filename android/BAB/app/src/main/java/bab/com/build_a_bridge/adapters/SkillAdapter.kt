package bab.com.build_a_bridge.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import bab.com.build_a_bridge.objects.Skill
import bab.com.build_a_bridge.utils.FirebaseRequestHandler
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.skill_list_item.view.*


abstract class SkillAdapter(var skillList: ArrayList<Skill>, val context: Context) :
        RecyclerView.Adapter<SkillAdapter.SkillHolder>() {

   abstract fun onItemClick(position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SkillHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.skill_list_item, parent, false)
        return SkillHolder(view)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        holder.skillNameTextView.text = skillList[position].name
        holder.skillDescriptionTextView.text = skillList[position].description



        val storageRef = FirebaseStorage.getInstance().reference
                .child(FirebaseStorageNames.SKILL_ICONS.toString())
                .child(skillList[position].id)

        storageRef.downloadUrl.addOnSuccessListener {
            val picassoInstance = Picasso.Builder(context)
                    .addRequestHandler(FirebaseRequestHandler()).build()

            // TODO: Image loading is slow here.
            picassoInstance.load(it).into(holder.skillIcon)
        }
    }


    inner class SkillHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillNameTextView: TextView = itemView.skill_name
        val skillDescriptionTextView: TextView = itemView.skill_description
        val skillIcon: AppCompatImageView = itemView.skill_icon


        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }
}