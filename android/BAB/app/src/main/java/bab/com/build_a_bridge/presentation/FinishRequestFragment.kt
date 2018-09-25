package bab.com.build_a_bridge.presentation


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.RatingBar
import android.widget.TextView
import bab.com.build_a_bridge.MainActivityViewModel

import bab.com.build_a_bridge.R
import bab.com.build_a_bridge.enums.FirebaseStorageNames
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


class FinishRequestFragment : DialogFragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java) }

    companion object {
        private const val VOLUNTEER_NAME = "volunteer_name"
        const val EXTRA_RATING = "volunteer_rating"


        fun newInstance(name: String, volunteerId: String?): FinishRequestFragment {
            val args = Bundle()
            args.putString(VOLUNTEER_NAME, name)


            val fragment = FinishRequestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_finish_request, content_frame)

        val userName = arguments?.getString(VOLUNTEER_NAME)
        val nameTextView: TextView = view.find(R.id.finish_request_name_tv)
        nameTextView.text = userName

        val ratingLabel: TextView = view.find(R.id.rating_label_tv)
        ratingLabel.text = getString(R.string.rating_label, userName)

        val volunteerImage: CircleImageView = view.find(R.id.finish_request_civ)
        val volunteerId = arguments?.getString(viewModel.requestForDetails.volunteerId)

        volunteerId?.let {
            val imgDbRef = FirebaseStorage.getInstance().reference
                    .child(FirebaseStorageNames.PROFILE_PICTURES.toString())
                    .child(it)

            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(imgDbRef)
                    .into(volunteerImage)
        }


        return AlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton(android.R.string.ok, object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val ratingBar: RatingBar = view.find(R.id.rating_bar)
                        val rating = ratingBar.rating
                        sendResult(Activity.RESULT_OK, rating)
                    }

                })
                .setNeutralButton(android.R.string.cancel, null)
                .create()
    }

    private fun sendResult(resultCode: Int, rating: Float){
        if(targetFragment == null)
            return
        val intent = Intent()
        intent.putExtra(EXTRA_RATING, rating)

        targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
    }


}
