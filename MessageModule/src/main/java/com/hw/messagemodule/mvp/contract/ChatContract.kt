package com.hw.messagemodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.messagemodule.data.bean.ChatBean

/**
 *author：pc-20171125
 *data:2020/1/8 14:34
 */
interface ChatContract {
    interface View : IBaseView {
        //第一次加载消息
        fun firstLoadMessage(chatBean: Array<ChatBean>)

        //发送消息成功
        fun sendMessageSuccess(chatBean: ChatBean)

        //发送消息失败
        fun sendMessageFaile(errorMsg:String)
    }

    interface Presenter {
        //发送新消息
        fun sendMessage(chatBean: ChatBean)

    }
}