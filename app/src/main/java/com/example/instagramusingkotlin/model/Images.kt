package com.example.instagramusingkotlin.model

class Images {
    var imageId:String ?  = null
    var imageUrl :String ? =null

    constructor()
    constructor(imageId: String?, imageUrl: String?) {
        this.imageId = imageId
        this.imageUrl = imageUrl
    }

}