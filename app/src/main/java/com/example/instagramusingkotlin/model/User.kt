package com.example.instagramusingkotlin.model

import java.io.Serializable

class User:Serializable {
    var uid:String ? = null
    var fullName : String?=null
    var userName : String?=null
    var email : String?=null
    var passowrd : String?=null
    var bio : String?=null
    var imageUrl : String?=null

    constructor()
    constructor(
        uid: String?,
        fullName: String?,
        userName: String?,
        email: String?,
        passowrd: String?,
        bio: String?,
        imageUrl: String?
    ) {
        this.uid = uid
        this.fullName = fullName
        this.userName = userName
        this.email = email
        this.passowrd = passowrd
        this.bio = bio
        this.imageUrl = imageUrl
    }


}