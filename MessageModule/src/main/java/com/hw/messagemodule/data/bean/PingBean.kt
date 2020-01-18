package com.hw.messagemodule.data.bean

data class PingBean(
    val data: Data,
    val message: Any,
    val responseCode: Int) {
    data class Data(
        //当前账号在其他地方登录
        val isPhoneRemove: Int,//0:正常，非0在其他地方登录
        //socket是否在线
        val isWebSocketOnline: Int,//1:socket在线
        val licenseMsg: String,
        val licenseStatus: Int
    )
}