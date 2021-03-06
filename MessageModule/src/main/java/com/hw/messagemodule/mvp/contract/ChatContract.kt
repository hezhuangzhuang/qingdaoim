package com.hw.messagemodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.provider.chat.bean.ChatBean
import com.hw.provider.chat.bean.MessageBody
import java.io.File

/**
 *author：pc-20171125
 *data:2020/1/8 14:34
 */
interface ChatContract {
    interface View : IBaseView {
        //第一次加载消息
        fun firstLoadMessage(chatBean: Array<ChatBean>)

        //发送消息成功
        fun sendMessageSuccess(messageBody: MessageBody)

        //发送消息失败
        fun sendMessageFaile(errorMsg: String)

        /**
         * 上传图片失败
         */
        fun uploadPhotoFaile(errorMsg: String)

        /**
         * 上传图片成功
         */
        fun uploadPhotoSuccess(
            //图片名称
            fileName: String,
            //图片的网络路径
            filePath: String
        )

        /**
         * 上传语音失败
         */
        fun uploadVoiceFaile(errorMsg: String)

        /**
         * 上传语音成功
         */
        fun uploadVoiceSuccess(
            fileLocalPath:String,
            //图片名称
            fileNetWorkName: String,
            //图片的网络路径
            fileNetWorkPath: String,
            duration: Int
        )


//        //发送图片成功
//        fun sendImageSuccess(messageBody: MessageBody)
//
//        //发送图片失败
//        fun sendImageFaile(errorMsg: String)
    }

    interface Presenter {
        //发送文本消息
        fun sendMessage(messageBody: MessageBody)

        //发送图片消息
        fun sendImage(messageBody: MessageBody)

        //发送语音消息
        fun sendVoice(messageBody: MessageBody)

        //上传图片
        fun uploadPhoto(file: File)

        //上传语音文件
        fun uploadVoice(file: File, duration: Int)

    }
}