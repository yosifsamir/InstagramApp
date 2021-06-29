package com.example.instagramusingkotlin.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.fragment.ProfileFragment
import com.theophrast.ui.widget.SquareImageView

class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var profileImage: SquareImageView =itemView.findViewById(R.id.profile_image)
}