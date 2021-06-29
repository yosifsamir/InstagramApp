package com.example.instagramusingkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.instagramusingkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    var fullName : String?=null
    var userName : String?=null
    var email : String?=null
    var passowrd : String?=null
    var bio : String="Hi, You are in instgram"
    var imageUrl : String="https://firebasestorage.googleapis.com/v0/b/instagram-1f3b5.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=72b7d33f-1924-44ed-aec8-4be2ceafeeb2"

    lateinit var fullNameEdt : EditText
    lateinit var userNameEdt : EditText
    lateinit var emailEdt : EditText
    lateinit var passwordEdt : EditText
    lateinit var signUpBtn : Button

    var firebaseDatabase:FirebaseDatabase?=null
    var databaseReference:DatabaseReference ? =null
    var firebaseAuth : FirebaseAuth ? =null
    var firebaseUser : FirebaseUser ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initializeViews()
        initialzeListener()
        firebaseInitialize()
    }

    private fun initializeViews() {
        fullNameEdt=findViewById(R.id.fullName_sign_up_edt)
        userNameEdt=findViewById(R.id.userName_sign_up_edt)
        emailEdt=findViewById(R.id.email_sign_up_edt)
        passwordEdt=findViewById(R.id.password_sign_up_edt)
        signUpBtn=findViewById(R.id.sign_up_from_sing_up_btn)

    }

    private fun initialzeListener() {
        signUpBtn.setOnClickListener({
            fullName=fullNameEdt.text.toString()
            userName=userNameEdt.text.toString()
            email=emailEdt.text.toString()
            passowrd=passwordEdt.text.toString()
            checkValidation()

        })
    }

    private fun checkValidation() {
        if (fullName==null || fullName!!.isEmpty()){
            fullNameEdt.setError("Enter your Full Name")
            fullNameEdt.requestFocus()
            return
        }
        if (userName==null || userName!!.isEmpty()){
            userNameEdt.setError("Enter your UserName")
            userNameEdt.requestFocus()
            return
        }
        if (email==null || email!!.isEmpty()){
            emailEdt.setError("Enter your Email")
            emailEdt.requestFocus()
            return
        }

        if (passowrd==null || passowrd!!.isEmpty()){
            passwordEdt.setError("Enter your Password")
            passwordEdt.requestFocus()
            return
        }
        if (passowrd!!.length <6){
            passwordEdt.setError("Your password is less than 7")
            passwordEdt.requestFocus()
            return
        }
        signUpUser(this!!.email, this!!.passowrd)

    }

    private fun firebaseInitialize() {
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase!!.getReference("Users")
        firebaseAuth= FirebaseAuth.getInstance()
    }

    private fun signUpUser(email:String?,password:String?) {
        var progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Waiting for sign up ....")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        firebaseAuth!!.createUserWithEmailAndPassword(email,passowrd).addOnCompleteListener({
            if (it.isSuccessful){
                val user:User=User(firebaseAuth!!.uid,fullName,userName,email,passowrd,bio,imageUrl)
                createUserDatabase(user,progressDialog)

            }
            else{
                val message=it.exception!!.message
                Toast.makeText(this@SignUpActivity,"${message}",Toast.LENGTH_LONG).show()
                firebaseAuth!!.signOut()
                return@addOnCompleteListener
                progressDialog.dismiss()
            }


        })
    }

    private fun createUserDatabase(user: User,progressDialog: ProgressDialog) {
        firebaseUser= firebaseAuth!!.currentUser
        databaseReference!!.child(firebaseUser!!.uid).setValue(user).addOnCompleteListener({
            if (it.isSuccessful){
                progressDialog.dismiss()
                firebaseDatabase!!.getReference("Follow")
                    .child(user.uid!!)
                    .child("Followering")
                    .child(user.uid!!).setValue(true)

                var intent= Intent(this@SignUpActivity,MainActivity::class.java)
                intent.putExtra("user",user)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            else{
                val message=it.exception!!.message
                Toast.makeText(this@SignUpActivity,"Check your connection or data${message}",Toast.LENGTH_LONG).show()
                firebaseAuth!!.signOut()
                return@addOnCompleteListener
                progressDialog.dismiss()
            }
        })
    }
}
