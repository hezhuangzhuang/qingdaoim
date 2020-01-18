package com.hw.messagemodule.data.api

import com.hw.messagemodule.data.bean.PingBean
import com.hw.messagemodule.data.bean.UploadBeanRespone
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *author：pc-20171125
 *data:2020/1/14 18:05
 */
interface ChatApi {
    /**
     * 上传文件
     */
    @Multipart
    @POST("fileAction_uploadFile.action")
    fun upload(@Part file: MultipartBody.Part): Observable<UploadBeanRespone>

    /**
     * 心跳
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/heartMap")
    fun ping(
        @Query("sip")      sip: String,
        @Query("deviceID") deviceID: String
    ): Observable<PingBean>
}