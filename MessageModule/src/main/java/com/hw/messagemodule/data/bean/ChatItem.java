package com.hw.messagemodule.data.bean;

import com.chad.library.adapter.base.entity.SectionMultiEntity;

/**
 * author：pc-20171125
 * data:2020/1/12 18:26
 */
public class ChatItem extends SectionMultiEntity<ChatBean> {
    public ChatBean chatBean;

    public ChatItem(ChatBean chatBean) {
        super(chatBean);
        this.chatBean = chatBean;
    }

    @Override
    public int getItemType() {
        return chatBean.messageType;
    }
}
