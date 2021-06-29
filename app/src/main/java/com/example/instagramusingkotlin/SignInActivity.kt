package com.example.instagramusingkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.instagramusingkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.Serializable

class SignInActivity : AppCompatActivity() {
    var emailEdt:EditText ?=null
    var passwordEdt:EditText ?=null
    var signInBtn:Button ?= null
    val refStorage :StorageReference ? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViews()
        signInBtn!!.setOnClickListener({
            checkEmailAndPassword()
        })
    }

    private fun checkEmailAndPassword() {
        var email=emailEdt!!.text.toString()
        var password=passwordEdt!!.text.toString()
        if (email==null ||email.isEmpty() || password==null || password.isEmpty() ){
            Toast.makeText(this@SignInActivity,"Check your email and password",Toast.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener({
            if (it.isSuccessful){
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user=snapshot.getValue(User::class.java)
                            var intent : Intent=Intent(this@SignInActivity,MainActivity ::class.java)
                            intent.putExtra("user",user)
                            startActivity(intent)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
//                var intent : Intent=Intent(this@SignInActivity,MainActivity ::class.java)
//                startActivity(intent)
            }
        })
    }

    private fun initViews() {
        emailEdt=findViewById(R.id.email_sign_in_edt)
        passwordEdt=findViewById(R.id.password_sign_in_edt)
        signInBtn=findViewById(R.id.sign_in_btn)
    }

    fun signUp(view: View) {
        startActivity(Intent(this@SignInActivity,SignUpActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
//        if (FirebaseAuth.getInstance().currentUser.uid !=null){
//            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(
//                object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        var user=snapshot.getValue(User::class.java)
//                        Toast.makeText(this@SignInActivity,"${user!!.imageUrl}",Toast.LENGTH_LONG).show()
//                        var intent : Intent=Intent(this@SignInActivity,MainActivity ::class.java)
//                        intent.putExtra("user",user)
//                        startActivity(intent)
//                        finish()
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//                })
//            finish()
//        }
    }
}
