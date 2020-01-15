package com.hw.messagemodule.data.bean

/**
 *author：pc-20171125
 *data:2020/1/14 13:52
 */
data class WebSocketResult(
    //状态码,-1:失败，0：成功
    var code: Int,
    //事件，对应OFFLINE_MESSAGE，CONNECTION_FAIL，PROCESS_MESSAGES_FAIL，MESSAGE
    var event: String,
    var data: Any
) {
    companion object {
        /*离线消息*/
        val OFFLINE_MESSAGE = "offlineMessage"

        /*连接失败*/
        val CONNECTION_FAIL = "connectionFail"

        /*处理消息失败*/
        val PROCESS_MESSAGES_FAIL = "ProcessMessagesFail"

        /*常规消息*/
        val MESSAGE = "message"
    }
}