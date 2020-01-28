package com.hw.contactsmodule.data.bean

data class GroupDetailsBean(
    val `data`: Data,
    val message: String,
    val responseCode: Int
){
    data class Data(
        val ifCreateUser: Boolean,
        val ifGroupExist: Int
    )
}