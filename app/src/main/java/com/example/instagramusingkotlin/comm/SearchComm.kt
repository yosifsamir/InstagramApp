package com.example.instagramusingkotlin.comm

import com.example.instagramusingkotlin.model.User

interface SearchComm {
    fun sendUser(user:User)
}