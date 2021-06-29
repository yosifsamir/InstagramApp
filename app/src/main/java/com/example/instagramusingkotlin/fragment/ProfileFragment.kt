package com.example.instagramusingkotlin.fragment


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.instagramusingkotlin.R
import com.example.instagramusingkotlin.adapter.ProfileAdapter
import com.example.instagramusingkotlin.comm.SearchComm
import com.example.instagramusingkotlin.model.Images
import com.example.instagramusingkotlin.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), SearchComm {
    var user:User ? = null
    var postsTxtNumebr : TextView ? =null
    var followersTxtNumber : TextView ? =null
    var followingTxtNumber : TextView ? =null
    var circleImageView:CircleImageView ?=null
    var recycleView:RecyclerView ?=null
    var editBtn: Button?=null

    var firebaseDatabase:FirebaseDatabase ?=null
    var databaseReference : DatabaseReference ?=null
    var imageUrl: Uri ? =null
    val PICK_IMAGE_REQUEST: Int = 22

    var context2:Context?=null
    var imagePath :String ? =null
    var listOfImages:MutableList<Images> ? = null
    var profileAdapter:ProfileAdapter ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase!!.getReference("Follow")
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context2=context
        listOfImages= mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        attachUsersToViews()
        Toast.makeText(context2,"${user!!.fullName}",Toast.LENGTH_LONG).show()
        listenerCircularView()
        initRecyclerView()
        return view
    }



    private fun listenerCircularView() {
        var picasso=Picasso.get()
//        picasso.isLoggingEnabled=true
//        picasso.load(user!!.imageUrl).into(circleImageView)
        Glide.with(this).load(user!!.imageUrl).into(circleImageView!!)
        circleImageView!!.setOnClickListener({
            SelectImage()
        })
        editBtn!!.setOnClickListener({
            var alertDialog:AlertDialog.Builder=AlertDialog.Builder(context2!!)
            alertDialog.setMessage("Are you sure to save change ?")
            alertDialog.setNegativeButton("Ok", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    storeUrlInFirebase(imageUrl)
                    Toast.makeText(context2,"The Image is stored successfully",Toast.LENGTH_LONG).show()

                }

            })
            alertDialog.setPositiveButton("cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Toast.makeText(context2,"the image is shown in your profile but not saved",Toast.LENGTH_LONG).show()
                }

            })
            alertDialog.create()
            alertDialog.show()

        })
    }

    private fun storeUrlInFirebase(imageUrl: Uri?) {
        if (imageUrl != null) {
            val fileName = System.currentTimeMillis().toString()+".jpg"

//            val database = FirebaseDatabase.getInstance()
            val uploadTask: StorageTask<*>
            val refStorage = FirebaseStorage.getInstance().reference.child("images/${user!!.uid}/$fileName")

            uploadTask = refStorage.putFile(imageUrl)
                .addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                        }
                    })

                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
            uploadTask.continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                @Throws(Exception::class)
                override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!task.isSuccessful()) {
                        throw task.getException()!!
                    }
                    return refStorage!!.getDownloadUrl()
                }
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful){
                    imagePath = task.result.toString()
                    Toast.makeText(context2, imagePath!!.toString(), Toast.LENGTH_SHORT).show()


                    saveImageToFirebase()
                }

//            saveToDB(username, email, imagePath.toString(), id)
            })
        }
    }

    private fun saveImageToFirebase() {
        var databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid!!).child("imageUrl")
        databaseReference.setValue(imagePath).addOnCompleteListener({
            if (it.isSuccessful){
                Toast.makeText(context2, "the image has changed", Toast.LENGTH_LONG).show()
                saveImageToListOfImages()
            }
        })
    }

    private fun saveImageToListOfImages() {

        val databaseReference=FirebaseDatabase.getInstance().getReference("Images").child(user!!.uid!!)
        val image:Images= Images(databaseReference.push().key.toString(),imagePath)
        databaseReference.child(image.imageId!!).setValue(imagePath)
    }

    private fun SelectImage() {


        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
            .setAspectRatio(1, 1)
            .start(context2!!,this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUrl = result.uri
                circleImageView!!.setImageURI(imageUrl)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


    private fun initViews(view:View) {
        postsTxtNumebr=view!!.findViewById(R.id.posts_profile_number)
        followersTxtNumber=view!!.findViewById(R.id.followers_profile_number)
        followingTxtNumber=view!!.findViewById(R.id.following_profile_number)
        circleImageView=view!!.findViewById(R.id.profile_img)
        editBtn=view!!.findViewById(R.id.edit_profile_btn)
        recycleView=view!!.findViewById(R.id.profile_images_recycler)

    }

    private fun attachUsersToViews() {
//        followingTxtNumber!!.text=getFolloweringNumber()
        getFolloweringNumber()
        getFollowersNumber()
    }

    private fun getFollowersNumber() {
//        var query=databaseReference!!.orderByKey().equalTo(user!!.uid).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//                    var total=snapshot.childrenCount
//                    followersTxtNumber!!.text=total.toString()
//                    Toast.makeText(context2,"${snapshot.key}",Toast.LENGTH_LONG).show()
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })



    }

    private fun getFolloweringNumber() {
        databaseReference!!.child(user!!.uid!!).child("Followering").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var total=snapshot.childrenCount.toString()
                    followingTxtNumber!!.text=total
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }


    private fun initRecyclerView() {
        var gridLayoutManager=GridLayoutManager(context2,3)
        recycleView!!.layoutManager=gridLayoutManager
        profileAdapter= ProfileAdapter(listOfImages!!)
        recycleView!!.adapter=profileAdapter

        FirebaseDatabase.getInstance().getReference("Images").child(user!!.uid!!).addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfImages!!.clear()
                    if (snapshot.exists()){
                        for (image in snapshot.children){
                            var images:Images= Images(image.key,image.getValue(String::class.java))
                            listOfImages!!.add(images)
                        }
                        Toast.makeText(context2,"the size of images -> ${listOfImages!!.size}",Toast.LENGTH_LONG).show()
                        listOfImages!!.reverse()
                        profileAdapter!!.notifyDataSetChanged()

                    }
                }
            })

    }

    override fun sendUser(user: User) {
        this.user=user
    }


    class SQuareImageView :ImageView{
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
