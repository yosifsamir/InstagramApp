package com.example.instagramusingkotlin.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_search_user.view.*

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var circleImageView:CircleImageView = itemView.findViewById(R.id.circleImageViewSearch)
    var userNameTxt:TextView = itemView.findViewById(R.id.userNameSearch)
    var fullNameTxt:TextView = itemView.findViewById(R.id.fullNameSearch)
    var addButton: Button = itemView.findViewById(R.id.addFriendSearch)
}