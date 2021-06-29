package com.example.instagramusingkotlin.fragment


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.adapter.SearchAdapter
import com.example.instagramusingkotlin.model.User
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    var searchEdt:EditText ? =null
    var searchImageBtn : ImageView ? =null
    var recyclerView : RecyclerView ? = null

    var searchAdapter:SearchAdapter ? =null
    var listOfUser:MutableList<User> = mutableListOf()
    var context2:Context ? =null

    lateinit var firebaseDatabase :FirebaseDatabase
    lateinit var databaseReference : DatabaseReference

    var search:String ? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context2=context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.getReference("Users")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_search, container, false)
        searchEdt=view.findViewById(R.id.searchEdt)
        searchImageBtn=view.findViewById(R.id.searchImg)
        recyclerView=view.findViewById(R.id.recyler_Home_fragment)

        searchImageBtn!!.setOnClickListener({
            search=searchEdt!!.text.toString()
            if (search==""){

            }else{
                Toast.makeText(context2,"${search}",Toast.LENGTH_LONG).show()

                        showAllUser(search!!.toLowerCase())
            }

        })

        initialzeRecycle()
        searchEdt!!.addTextChangedListener{
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    search=searchEdt!!.text.toString()

                    Toast.makeText(context2,"${search}",Toast.LENGTH_LONG).show()

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    search=searchEdt!!.text.toString()
                    if (search==""){

                    }else{
                        Toast.makeText(context2,"${search}",Toast.LENGTH_LONG).show()

//                        showAllUser(search!!.toLowerCase())
                    }
                }
            }}
        return view
    }

    private fun initialzeRecycle() {
        recyclerView!!.layoutManager=LinearLayoutManager(context2,LinearLayoutManager.VERTICAL,false)
        recyclerView!!.setHasFixedSize(true)
        searchAdapter=SearchAdapter(listOfUser!!)
        recyclerView!!.adapter=searchAdapter
    }

    private fun showAllUser(search: String) {
        Toast.makeText(context2,"${search}",Toast.LENGTH_LONG).show()

        val query: Query =databaseReference.orderByChild("fullName")
            .startAt(search)
            .endAt(search+"\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listOfUser!!.clear()
                    var user:User ? =null
                    for(dataSnapshot in snapshot.children){
                        user=dataSnapshot.getValue(User::class.java)
                        listOfUser!!.add(user!!)
                        Toast.makeText(context2,"${listOfUser.size}",Toast.LENGTH_LONG).show()
                    }
                    searchAdapter!!.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(context2,"no Exists data",Toast.LENGTH_LONG).show()

                    listOfUser!!.clear()
                    searchAdapter!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
            
        })
    }

}
