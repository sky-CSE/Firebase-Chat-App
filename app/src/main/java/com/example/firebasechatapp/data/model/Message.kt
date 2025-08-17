package com.example.firebasechatapp.data.model

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timeStamp: Long = 0L
)
