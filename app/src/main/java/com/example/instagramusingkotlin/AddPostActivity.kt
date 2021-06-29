package com.example.instagramusingkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.instagramusingkotlin.model.Post
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AddPostActivity : AppCompatActivity() {

    var okBtn:ImageView ?=null
    var cancelBtn:ImageView ? =null
    var postEdt:EditText ? =null
    var imageView:ImageView ? =null

    var storageReference:StorageReference ? =null
    var firebaseDatabase : FirebaseDatabase ? =null
    var databaseReference : DatabaseReference ? =null
    var imageUrl: Uri? =null
    var imagePath:String ?=null
    var description:String ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        initViews()
        initFirebase()
        listenersViews()
    }



    private fun initViews() {
        okBtn=findViewById(R.id.ok__post_btn)
        cancelBtn=findViewById(R.id.cancel_post_btn)
        postEdt=findViewById(R.id.post_description_edt)
        imageView=findViewById(R.id.image_post)
    }

    private fun initFirebase() {
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase!!.getReference("Posts")

    }

    private fun listenersViews() {
        imageView!!.setOnClickListener({
            SelectImage()
        })

        okBtn!!.setOnClickListener({
            if (imageUrl==null){
                return@setOnClickListener
            }
            description=postEdt!!.text.toString()
            if (description==null){
                return@setOnClickListener
            }
            addPostToFirebase()
        })
    }

    private fun addPostToFirebase() {
        val progressDialog=ProgressDialog(this@AddPostActivity)

        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("wait until finish processing")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val uploadTask: StorageTask<*>
        var imagStorgePath :String=System.currentTimeMillis().toString()+".jpg"
        storageReference=FirebaseStorage.getInstance().reference.child("Posts_image").child(imagStorgePath)
        uploadTask = storageReference!!.putFile(imageUrl!!)
        uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            @Throws(Exception::class)
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                if (!task.isSuccessful()) {
                    throw task.getException()!!
                    progressDialog.dismiss()
                }
                return storageReference!!.getDownloadUrl()
            }
        }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
            if (task.isSuccessful){
                progressDialog.dismiss()
                imagePath = task.result.toString()
                Toast.makeText(this@AddPostActivity, imagePath!!.toString(), Toast.LENGTH_SHORT).show()
                saveAllDetailsToFirebase(progressDialog)
            }

//            saveToDB(username, email, imagePath.toString(), id)
        })
    }

    private fun saveAllDetailsToFirebase(progressDialog: ProgressDialog) {
        var post:Post= Post(databaseReference!!.push().key.toString(),description,FirebaseAuth.getInstance().currentUser.uid,imagePath,0,0,
            emptyList())
        databaseReference!!.child(post.postId!!).setValue(post).addOnCompleteListener({
            if (it.isSuccessful){
                progressDialog.dismiss()
                super.finish()

            }
        })
    }

    private fun SelectImage() {


        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUrl = result.uri
                Glide.with(this).load(imageUrl).into(imageView!!)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}
