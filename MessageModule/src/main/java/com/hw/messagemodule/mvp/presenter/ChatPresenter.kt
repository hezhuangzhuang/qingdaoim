package com.hw.messagemodule.mvp.presenter

import com.hw.baselibrary.common.BasePresenter
import com.hw.messagemodule.data.bean.MessageBody
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.model.ChatService
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 14:35
 */
class ChatPresenter @Inject constructor() : BasePresenter<ChatContract.View>(),
    ChatContract.Presenter {


    @Inject
    lateinit var chatService: ChatService

    /**
     * 发送消息
     */
    override fun sendMessage(messageBody: MessageBody) {
        chatService.sendMessage(messageBody)
    }
}