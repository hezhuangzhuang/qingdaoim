package com.hw.messagemodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.baselibrary.utils.DateUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatBeanLastMessage
import com.hw.provider.chat.ChatMultipleItem

/**
 *author：pc-20171125
 *data:2020/1/8 10:28
 */
class HomeMessageAdapter(layoutResId: Int, data: MutableList<ChatBeanLastMessage>?) :
    BaseQuickAdapter<ChatBeanLastMessage, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ChatBeanLastMessage?) {
        helper.setText(R.id.tvName, item?.conversationUserName)
            .setText(R.id.tvContent, getShowText(item!!))
            .setText(R.id.tvTime, DateUtils.getTimeStringAutoShort2(item!!.time, true))
            .setVisible(R.id.viewReadStatus, !item.isRead)
    }

    /**
     * 根据类型返回显示的文本
     */
    fun getShowText(item: ChatBeanLastMessage): String {
        when (item.messageType) {
            //语音
            ChatMultipleItem.SEND_VOICE, ChatMultipleItem.FORM_VOICE -> {
                val isVoiceFile =
                    item!!.textContent.endsWith(".voice") || item.textContent.endsWith(".m4a")
                if (isVoiceFile) {
                    return "[语音]"
                } else {
                    return "[文件]"
                }
            }

            //图片
            ChatMultipleItem.SEND_IMG, ChatMultipleItem.FORM_IMG -> return "[图片]"

            //文字
            ChatMultipleItem.SEND_TEXT, ChatMultipleItem.FORM_TEXT -> return item.textContent

            //视频通话
            ChatMultipleItem.SEND_VIDEO_CALL, ChatMultipleItem.FORM_VIDEO_CALL -> return "[视频通话]"

            //语音通话
            ChatMultipleItem.SEND_VOICE_CALL, ChatMultipleItem.FORM_VOICE_CALL -> return "[语音通话]"

            else -> return ""
        }
    }
}