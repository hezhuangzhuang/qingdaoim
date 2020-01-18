package com.hw.messagemodule.mvp.model

import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.messagemodule.data.api.ChatApi
import com.hw.messagemodule.data.bean.PingBean
import com.hw.messagemodule.data.bean.UploadBeanRespone
import com.hw.messagemodule.service.KotlinMessageSocketService
import com.hw.provider.chat.bean.MessageBody
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 14:35
 */
class ChatService @Inject constructor() {

    /**
     * 发送消息
     */
    fun sendMessage(messageBody: MessageBody): Boolean {
        return KotlinMessageSocketService.sendMessage(messageBody)
    }


    /**
     * 发送图片
     */
    fun sendImage(messageBody: MessageBody): Boolean {
        return KotlinMessageSocketService.sendMessage(messageBody)
    }

    /**
     * 上传图片
     */
    fun uploadPhoto(file: File): Observable<UploadBeanRespone> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("uploadFile", file.getName(), requestFile)

        return RetrofitManager
            .create(ChatApi::class.java, Urls.FILE_URL)
            .upload(filePart)
            .compose(CustomCompose())
    }

    /**
     * 心跳的接口
     */
    fun ping(
        sip: String,
        deviceID: String
    ): Observable<PingBean> {
        return RetrofitManager
            .create(ChatApi::class.java, Urls.WEBSOCKET_URL)
            .ping(sip, deviceID)
            .compose(CustomCompose())
    }
}