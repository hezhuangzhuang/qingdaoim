package com.hw.messagemodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.baselibrary.utils.DateUtils
import com.hw.messagemodule.R
import com.hw.provider.chat.bean.ChatBeanLastMessage
import com.hw.provider.chat.ChatMultipleItem

/**
 *author：pc-20171125
 *data:2020/1/8 10:28
 */
class HomeMessageAdapter(layoutResId: Int, data: MutableList<ChatBeanLastMessage>?) :
    BaseQuickAdapter<ChatBeanLastMessage, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ChatBeanLastMessage?) {
        helper.setText(R.id.tvName, item?.conversationUserName)
            .setText(R.id.tvContent, getShowText(item!!, item.isGroup))
            .setText(R.id.tvTime, DateUtils.getTimeStringAutoShort2(item!!.time, true))
            .setVisible(R.id.viewReadStatus, !item.isRead)
    }

    /**
     * 根据类型返回显示的文本
     */
    fun getShowText(item: ChatBeanLastMessage, isGroupChat: Boolean): String {
        when (item.messageType) {
            //语音
            ChatMultipleItem.SEND_VOICE -> {
                val isVoiceFile =
                    item!!.textContent.endsWith(".voice") || item.textContent.endsWith(".m4a")
                if (isVoiceFile) {
                    return "[语音]"
                } else {
                    return "[文件]"
                }
            }

            ChatMultipleItem.FORM_VOICE -> {
                val isVoiceFile =
                    item!!.textContent.endsWith(".voice") || item.textContent.endsWith(".m4a")
                if (isVoiceFile) {
                    return if (isGroupChat) "${item.getName()}: [语音]" else "[语音]"
                } else {
                    return if (isGroupChat) "${item.getName()}: [文件]" else "[文件]"
                }
            }

            //图片
            ChatMultipleItem.SEND_IMG -> return "[图片]"
            ChatMultipleItem.FORM_IMG -> return if (isGroupChat) "${item.getName()}: [图片]" else "[图片]"

            //文字
            ChatMultipleItem.SEND_TEXT -> return item.textContent
            ChatMultipleItem.FORM_TEXT -> return if (isGroupChat) "${item.getName()}: ${item.textContent}" else item.textContent

            //视频通话
            ChatMultipleItem.SEND_VIDEO_CALL -> return "[视频通话]"
            ChatMultipleItem.FORM_VIDEO_CALL -> return if (isGroupChat) "${item.getName()}: [视频通话]" else "[视频通话]"

            //语音通话
            ChatMultipleItem.SEND_VOICE_CALL -> return "[语音通话]"

            ChatMultipleItem.FORM_VOICE_CALL -> return if (isGroupChat) "${item.getName()}: [语音通话]" else "[语音通话]"

            else -> return ""
        }
    }
}