package com.hw.messagemodule.mvp.presenter

import com.hw.baselibrary.common.BasePresenter
import com.hw.messagemodule.data.bean.ChatBeanLastMessage
import com.hw.messagemodule.mvp.contract.MessageContract
import com.hw.messagemodule.mvp.model.MessageService
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 09:55
 */
class MessagePresenter @Inject constructor() : BasePresenter<MessageContract.View>(),
    MessageContract.Presenter {


    @Inject
    lateinit var messageService: MessageService

    /**
     * 查询最后一条数据
     */
    override fun queryLastChatBeans() {
        checkViewAttached()
        mRootView?.showLoading()

        val queryLastChatBeans = messageService.queryLastChatBeans()
        mRootView?.apply {
            dismissLoading()
            refreshLastMessage(queryLastChatBeans)
        }
    }

    /**
     * 更新消息的阅读状态
     */
    override fun updateMessageReadStauts(chatBeanLastMessage: ChatBeanLastMessage) {
        messageService.updateMessageReadStauts(chatBeanLastMessage)
    }

}