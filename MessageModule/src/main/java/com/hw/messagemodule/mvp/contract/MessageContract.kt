package com.hw.messagemodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.provider.chat.bean.ChatBeanLastMessage

/**
 *author：pc-20171125
 *data:2020/1/8 09:53
 */
interface MessageContract {
    //
    interface View: IBaseView{
        //刷新最后一条数据
        fun refreshLastMessage(list:List<ChatBeanLastMessage>)
    }

    //
    interface Presenter{
        //刷新数据
        fun queryLastChatBeans()

        fun updateMessageReadStauts(chatBeanLastMessage: ChatBeanLastMessage)
    }
}