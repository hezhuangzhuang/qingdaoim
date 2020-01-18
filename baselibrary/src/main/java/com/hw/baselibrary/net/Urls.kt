package com.hw.baselibrary.net

import okhttp3.MediaType

/**
 *author：pc-20171125
 *data:2019/11/8 11:51
 */
object Urls{
    val MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")

    const val BASE_URL = "http://61.182.50.12:8085/xjdj/"

    //即时通讯和基础业务的路径
    const val WEBSOCKET_URL = "http://demo.szzxwl.com:9016/"

    //上传文件的服务器
    const val FILE_URL = "http://demo.szzxwl.com:9012/videoConf/"
}