package com.hw.provider.eventbus

/**
 *author：pc-20171125
 *data:2020/1/14 11:59
 * 发送evebtbus时的消息
 */
data class EventMsg<T>(
    var message: String,
    var messageData: T
) {

    companion object {
        //收到消息
        val RECEIVE_SINGLE_MESSAGE = "RECEIVE_SINGLE_MESSAGE"

        //刷新首页消息
        val REFRESH_HOME_MESSAGE = "REFRESH_HOME_MESSAGE"

        //更新消息的阅读状态
        val UPDATE_MESSAGE_READ_STATUS = "UPDATE_MESSAGE_READ_STATUS"

        //更新首页消息提醒
        val UPDATE_MAIN_NOTIF = "UPDATE_MAIN_NOTIF"
    }

}