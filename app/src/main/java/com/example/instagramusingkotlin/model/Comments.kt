package com.example.instagramusingkotlin.model

class Comments {
    var uid:String ?=null
    var username : String ?=null
    var comment:String ?=null

    constructor(){

    }

    constructor(uid: String?, username: String?, comment: String?) {
        this.uid = uid
        this.username = username
        this.comment = comment
    }


}