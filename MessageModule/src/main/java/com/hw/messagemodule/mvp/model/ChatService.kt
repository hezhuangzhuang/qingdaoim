package com.hw.messagemodule.mvp.model

import com.hw.messagemodule.data.bean.MessageBody
import com.hw.messagemodule.service.KotlinMessageSocketService
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
}