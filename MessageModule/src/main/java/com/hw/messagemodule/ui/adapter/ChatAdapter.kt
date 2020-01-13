package com.hw.messagemodule.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.baselibrary.image.ImageLoader
import com.hw.baselibrary.utils.DateUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatItem
import com.hw.provider.chat.ChatMultipleItem

/**
 *author：pc-20171125
 *data:2020/1/13 20:27
 */
class ChatAdapter : BaseSectionMultiItemQuickAdapter<ChatItem, BaseViewHolder> {
    constructor(sectionHeadResId: Int, data: MutableList<ChatItem>?) : super(
        sectionHeadResId,
        data
    ) {
        addItemType(ChatMultipleItem.SEND_TEXT, R.layout.item_send_msg)
        addItemType(ChatMultipleItem.FORM_TEXT, R.layout.item_from_msg)

        addItemType(ChatMultipleItem.SEND_VOICE, R.layout.item_send_voice)
        addItemType(ChatMultipleItem.FORM_VOICE, R.layout.item_from_voice)

        addItemType(ChatMultipleItem.SEND_IMG, R.layout.item_send_img)
        addItemType(ChatMultipleItem.FORM_IMG, R.layout.item_from_img)

        addItemType(ChatMultipleItem.SEND_VIDEO_CALL, R.layout.item_send_msg)
        addItemType(ChatMultipleItem.FORM_VIDEO_CALL, R.layout.item_from_msg)

        addItemType(ChatMultipleItem.SEND_VOICE_CALL, R.layout.item_send_msg)
        addItemType(ChatMultipleItem.FORM_VOICE_CALL, R.layout.item_from_msg)
    }

    override fun convertHead(helper: BaseViewHolder?, item: ChatItem?) {
    }

    override fun convert(helper: BaseViewHolder, item: ChatItem?) {
        var tvContent = helper.getView<TextView>(R.id.tv_content)

        when (item?.itemType) {
            ChatMultipleItem.SEND_TEXT,
            ChatMultipleItem.FORM_TEXT ->
                setTextContent(helper, item)

            ChatMultipleItem.SEND_VOICE,
            ChatMultipleItem.FORM_VOICE ->
                setVoiceContent(helper, item)

            ChatMultipleItem.FORM_IMG,
            ChatMultipleItem.FORM_IMG ->
                setImgContent(helper, item)


        }
    }

    /**
     * 设置图片消息内容
     */
    private fun setImgContent(helper: BaseViewHolder, item: ChatItem) {
        helper.addOnClickListener(R.id.iv_voice)
            .setText(R.id.tv_name, item.chatBean.name)
            .setText(R.id.tv_time, DateUtils.getTimeStringAutoShort2(item.chatBean.time, true))

        ImageLoader.with(mContext)
            .load(item.chatBean.textContent)
            .into(helper.getView(R.id.iv_voice) as ImageView)
    }

    /**
     * 设置语音消息内容
     */
    private fun setVoiceContent(helper: BaseViewHolder, item: ChatItem) {
        helper.addOnClickListener(R.id.fl_voice)
        //判断是否是语音文件
        val isVoice =
            item!!.chatBean.textContent.endsWith(".voice") || item.chatBean.textContent.endsWith(".m4a")
        if (isVoice) {
            helper.setImageResource(R.id.iv_voice, R.drawable.audio_animation_right_list)
                .setText(R.id.tv_duration, "")
                .setText(R.id.tv_name, item.chatBean.name)
                .setText(
                    R.id.tv_time, DateUtils.getTimeStringAutoShort2(item.chatBean.time, true)
                )
        } else {
            val temp = item.chatBean.textContent
            val fileName = temp.substring(temp.lastIndexOf("/") + 1)

            helper.setImageResource(R.id.iv_voice, R.mipmap.ic_file)
                .setText(R.id.tv_name, item.chatBean.name)
            helper.setText(R.id.tv_duration, fileName)
            helper.setText(
                R.id.tv_time,
                DateUtils.getTimeStringAutoShort2(item.chatBean.time, false)
            )
        }
    }

    /**
     * 设置文本消息内容
     */
    private fun setTextContent(helper: BaseViewHolder, item: ChatItem) {
        helper.setText(R.id.tv_name, item.chatBean.name)
        helper.setText(
            R.id.tv_time,
            DateUtils.getTimeStringAutoShort2(item.chatBean.time, true)
        )
        helper.setText(R.id.tv_content, item.chatBean.textContent)
    }
}