package com.uqac.actilink.models

data class UserProfile(
    val userId : String = "",
    val name: String = "",
    val age: Int = 0,
    val bio: String = "",
)