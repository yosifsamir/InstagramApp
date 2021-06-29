package com.example.instagramusingkotlin.adapter

import android.content.Context
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.comm.SearchComm
import com.example.instagramusingkotlin.fragment.ProfileFragment
import com.example.instagramusingkotlin.model.User
import com.example.instagramusingkotlin.viewholder.SearchViewHolder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class SearchAdapter : RecyclerView.Adapter<SearchViewHolder> {
    var listOfUsers : MutableList<User> ? =null
    var firebaseUser : FirebaseUser ? = null
    var firebaseDatabase : FirebaseDatabase ? =null
    var context2:Context ? =null
    constructor(listOfUsers: MutableList<User>){
        this.listOfUsers=listOfUsers
        firebaseUser=FirebaseAuth.getInstance().currentUser
        firebaseDatabase= FirebaseDatabase.getInstance()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        var view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_search_user,parent,false)
        context2=parent.context
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers!!.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        var user:User=listOfUsers!!.get(position)
        holder.userNameTxt.text=user.userName
        holder.fullNameTxt.text=user.fullName
        if (user.imageUrl!=null){
            Picasso.get().load(user.imageUrl).into(holder.circleImageView)
        }

        chechFollowingStatus(user.uid,holder.addButton)
        holder.itemView.setOnClickListener({
            showAllInformation(user)
        })

        holder.addButton.setOnClickListener({
          // Will be implemented as soon as possible
            if (holder.addButton.text=="Add"){
                firebaseDatabase!!.getReference("Follow")
                    .child(firebaseUser!!.uid)
                    .child("Followering")
                    .child(user.uid!!).setValue(true)
                    .addOnCompleteListener({
                        if (it.isSuccessful){
                            firebaseDatabase!!.getReference("Follow")
                                .child(user.uid!!)
                                .child("Followers")
                                .child(firebaseUser!!.uid).setValue(true)
                                .addOnCompleteListener({
                                    if (it.isSuccessful){

                                    }
                                })
                        }
                    })
            }
            else{
                firebaseDatabase!!.getReference("Follow")
                    .child(user.uid!!)
                    .child("Followering")
                    .child(firebaseUser!!.uid).removeValue()
                    .addOnCompleteListener({
                        if (it.isSuccessful){
                            firebaseDatabase!!.getReference("Follow")
                                .child(firebaseUser!!.uid)
                                .child("Followers")
                                .child(user.uid!!).removeValue()
                                .addOnCompleteListener({
                                    if (it.isSuccessful){

                                    }
                                })
                        }
                    })
            }
        })
    }

    private fun showAllInformation(user: User) {
        var supportFragmentManager = (context2 as FragmentActivity).supportFragmentManager
        var transaction=supportFragmentManager.beginTransaction()
        var profileFragment=ProfileFragment()
        var searchComm:SearchComm=profileFragment
        searchComm.sendUser(user)
        transaction.replace(R.id.frame_container,profileFragment)
        transaction.commit()

    }

    private fun chechFollowingStatus(uid: String?, addButton: Button) {
        val readUser=firebaseDatabase!!.getReference("Follow")
            .child(firebaseUser!!.uid)
            .child("Followers")
        readUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.child(uid!!).exists()) {
                   addButton.text="Remove"
               }
                else {
                   addButton.text="Add"
               }
            }
            override fun onCancelled(error: DatabaseError) {

            }


        })
    }
}