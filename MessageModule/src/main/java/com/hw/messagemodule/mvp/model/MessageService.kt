package com.hw.messagemodule.mvp.model

import com.hw.messagemodule.data.bean.ChatBeanLastMessage
import com.hw.messagemodule.db.GreenDaoUtil
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 09:57
 * 获取数据的地方
 */
class MessageService  @Inject constructor(){
    /**
     * 获取最近消息列表
     */
    fun queryLastChatBeans():List<ChatBeanLastMessage>{
        return GreenDaoUtil.queryLastChatBeans()
    }

    /**
     * 更新阅读状态
     */
    fun updateMessageReadStauts(chatBeanLastMessage: ChatBeanLastMessage){
        return GreenDaoUtil.insertLastChatBean(chatBeanLastMessage)
    }
}