package com.example.instagramusingkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.model.Images
import com.example.instagramusingkotlin.viewholder.ProfileViewHolder
import com.squareup.picasso.Picasso

class ProfileAdapter : RecyclerView.Adapter<ProfileViewHolder> {
    var listOfImages:MutableList<Images> ? = null

    constructor(listOfImages: MutableList<Images>){
        this.listOfImages=listOfImages
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        var view: View =LayoutInflater.from(parent.context).inflate(R.layout.profile_image_layout,parent,false)
        return ProfileViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfImages!!.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        var image:Images= this!!.listOfImages!![position]
        Picasso.get().load(image.imageUrl).into(holder.profileImage)

    }
}