package com.example.instagramusingkotlin.viewholder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import de.hdodenhof.circleimageview.CircleImageView

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var profileImagePost: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
    var profileNameText: TextView = itemView.findViewById(R.id.user_name_profile_post)
    var postImagePost: ImageView = itemView.findViewById(R.id.post_image_home)
    var likeBtn: ImageView = itemView.findViewById(R.id.post_image_like_btn)
    var commentBtn: ImageView = itemView.findViewById(R.id.post_image_comment_btn)
    var saveBtn: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
    var numberOfLikesTxt: TextView = itemView.findViewById(R.id.likes)
    var publisherNameTxt: TextView = itemView.findViewById(R.id.publisher)
    var descriptionTxt: TextView = itemView.findViewById(R.id.description)
    var commentTxt: TextView = itemView.findViewById(R.id.comments)

}