package com.example.instagramusingkotlin.model

class Post {
    var postId:String ? =null
    var description:String ? =null
    var publisher:String ?=null
    var post_image_url : String ?=null
    var likeCount : Int=0
    var commentsCount:Int=0
    var listOfComments:List<Comments> ? = emptyList()

    constructor(
        postId: String?,
        description: String?,
        publisher: String?,
        post_image_url: String?,
        likeCount:Int,
        commentsCount:Int,
        listOfComments:List<Comments>
    ) {
        this.postId = postId
        this.description = description
        this.publisher = publisher
        this.post_image_url = post_image_url
        this.likeCount=likeCount
        this.commentsCount=commentsCount
        this.listOfComments=listOfComments
    }

    constructor()
}