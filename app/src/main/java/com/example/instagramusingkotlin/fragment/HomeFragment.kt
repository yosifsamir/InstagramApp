package com.example.instagramusingkotlin.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.adapter.PostAdapter
import com.example.instagramusingkotlin.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var myView : View ? =null


    var recyclerView:RecyclerView ? =null

    var postAdapter:PostAdapter ? =null
    var listOfPost : MutableList<Post>?= mutableListOf<Post>()

    var listOfFollowering : MutableList<String> ? =null
    var firebaseDatabase: FirebaseDatabase? =null
    var firebaseAuth : FirebaseAuth? =null
    var databaseReference : DatabaseReference? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase= FirebaseDatabase.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        databaseReference=firebaseDatabase!!.getReference("Posts")
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView=inflater.inflate(R.layout.fragment_home, container, false)
        intializeRecycler()

        retrieveAllPost()

        return myView
    }


    private fun intializeRecycler() {
        recyclerView=myView!!.findViewById(R.id.recyler_Home_fragment)
        var linearLayoutManager:LinearLayoutManager =
            LinearLayoutManager(context,LinearLayoutManager.VERTICAL,true)
        linearLayoutManager.stackFromEnd=true
        recyclerView!!.layoutManager=linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        postAdapter = PostAdapter(listOfPost!!)
//        Toast.makeText(context,""+listOfPost!!.size,Toast.LENGTH_LONG).show()
        recyclerView!!.adapter=postAdapter
//        postAdapter = PostAdapter()

    }

    private fun retrieveAllPost() {
        firebaseDatabase!!.getReference("Follow")
            .child(firebaseAuth!!.uid!!)
            .child("Followering")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfFollowering= mutableListOf<String>()
                    listOfFollowering!!.clear()
                    if(snapshot.exists()){
                        for (data in snapshot.children){
                            listOfFollowering!!.add(data.key!!)
                        }

                        retrivePosts()
                    }
                }
            })
    }

    private fun retrivePosts() {
        if (listOfFollowering==null)
            return

        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                listOfPost!!.clear()

                postAdapter!!.notifyDataSetChanged()
                for(data in snapshot.children){
                    val post:Post=data.getValue(Post::class.java)!!
                    if (post.publisher in listOfFollowering!!){
                        listOfPost!!.add(post)
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }



    override fun onResume() {
        super.onResume()
    }
}
