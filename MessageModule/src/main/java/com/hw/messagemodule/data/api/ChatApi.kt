package com.hw.messagemodule.data.api

import com.hw.messagemodule.data.bean.UploadBeanRespone
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    abstract fun upload(@Part file: MultipartBody.Part): Observable<UploadBeanRespone>
}