package com.example.instagramusingkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.model.Post
import com.example.instagramusingkotlin.model.User
import com.example.instagramusingkotlin.viewholder.PostViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostViewHolder> {
    var listOfPosts:MutableList<Post> ? =null
    var firebaseDatabase:FirebaseDatabase ? =null
    var firebaseAuth : FirebaseAuth ? =null
    var databaseReference : DatabaseReference ? =null
    var databaseReferenceUser : DatabaseReference ? =null
    var databaseReferneceLikes : DatabaseReference ? =null
    var context2:Context ? =null
    constructor(listOfPosts : MutableList<Post>){
        this.listOfPosts=listOfPosts
        firebaseDatabase= FirebaseDatabase.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        databaseReference=firebaseDatabase!!.getReference("Posts")
        databaseReferneceLikes=firebaseDatabase!!.getReference("Likes")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        context2=parent.context
        var view:View=LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout,parent,false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfPosts!!.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        var post:Post= this!!.listOfPosts!![position]
        Picasso.get().load(post.post_image_url).into(holder.postImagePost)
        holder.descriptionTxt.text=post.description

        holder.descriptionTxt.text=post.description
        holder.commentTxt.text=post.commentsCount.toString()
        loadDataAboutUser(holder,post.publisher,post.postId)
    }

    private fun loadDataAboutUser(holder: PostViewHolder, userUId: String?,postId:String?) {

        databaseReferenceUser=firebaseDatabase!!.getReference("Users").child(userUId!!)
        databaseReferenceUser!!.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user:User= snapshot.getValue(User::class.java)!!
                        if (user==null)
                            return
                        Picasso.get().load(user!!.imageUrl).into(holder.profileImagePost)
                        holder.profileNameText.text=user!!.userName
                        loadLikesSize(postId,userUId,holder)


                    }
            })

    }

    private fun loadLikesSize(
        postId: String?,
        userUId: String?,
        holder: PostViewHolder
    ) {
        databaseReferneceLikes!!.child(postId!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var userIds=snapshot.children.map { it.key }
                var imageDrawable:Int = 0
                if (userUId in userIds){
                    holder.likeBtn.setImageResource(R.drawable.heart_clicked)
                    imageDrawable=R.drawable.heart_clicked
                }
                else{
                    holder.likeBtn.setImageResource(R.drawable.heart_not_clicked)
                    imageDrawable=R.drawable.heart_not_clicked
                }

                holder.likeBtn.setOnClickListener({
                    if(imageDrawable==R.drawable.heart_not_clicked){
                        Toast.makeText(context2,"you inside heart_not_clicked ",Toast.LENGTH_LONG).show()
                        databaseReferneceLikes!!.child(postId!!).child(userUId!!).setValue(true)
                        holder.likeBtn.setImageResource(R.drawable.heart_clicked)
                        imageDrawable=R.drawable.heart_clicked
                        holder.numberOfLikesTxt.text=userIds.size.toString()
                    }


//                    }
                    else {
                        databaseReferneceLikes!!.child(postId!!).child(userUId!!).removeValue()
                        imageDrawable=R.drawable.heart_not_clicked
                        holder.likeBtn.setImageResource(imageDrawable)
                        holder.numberOfLikesTxt.text=userIds.size.toString()
                    }

                })

                holder.numberOfLikesTxt.text=userIds.size.toString()
                Toast.makeText(context2,"${userIds.size}",Toast.LENGTH_LONG).show()

                ////
            }
        })

    }

}