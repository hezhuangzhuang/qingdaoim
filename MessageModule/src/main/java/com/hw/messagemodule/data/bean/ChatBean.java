package com.hw.messagemodule.data.bean;

/**
 * author：pc-20171125
 * data:2020/1/12 18:24
 */
public class ChatBean {
    //TODO:消息的id，应该设置自增加, @Id(autoincrement = true)
    public Long id;

    /**
     * 显示在列表中的消息类型
     * 对应MultipleItem
     */
    public int messageType;

    //发送人的名称
    public String name;

    //消息内容
    public String content;

    //时间
    public long sendDate;

    //true：发出去的消息
    //false：接收到的消息
    public boolean isSend;

    //true:已读
    //false：未读
    public boolean isRead;

    //true：群聊消息
    //false：点对点消息
    public boolean isGroup;

    //会话Id
    public String conversationId;

    //聊天界面顶部显示的名称
    public String conversationUserName;

    public ChatBean(
            int messageType,
            String name,
            String content, long sendDate, boolean isSend, boolean isRead, boolean isGroup, String conversationId, String conversationUserName) {
        this.messageType = messageType;
        this.name = name;
        this.content = content;
        this.sendDate = sendDate;
        this.isSend = isSend;
        this.isRead = isRead;
        this.isGroup = isGroup;
        this.conversationId = conversationId;
        this.conversationUserName = conversationUserName;
    }

    public ChatBean(Builder builder) {
        this.messageType = builder.messageType;
        this.name = builder.name;
        this.content = builder.content;
        this.sendDate = builder.sendDate;
        this.isSend = builder.isSend;
        this.isRead = builder.isRead;
        this.isGroup = builder.isGroup;
        this.conversationId = builder.conversationId;
        this.conversationUserName = builder.conversationUserName;
    }

    public static class Builder {
        private int messageType;

        //发送人的名称
        private String name;

        //消息内容
        private String content;

        //时间
        private long sendDate;

        //true：发出去的消息
        //false：接收到的消息
        private boolean isSend;

        //true:已读
        //false：未读
        private boolean isRead;

        //true：群聊消息
        //false：点对点消息
        private boolean isGroup;

        //会话Id
        private String conversationId;

        //聊天界面顶部显示的名称
        private String conversationUserName;

        public Builder setMessageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setSendDate(long sendDate) {
            this.sendDate = sendDate;
            return this;
        }

        public Builder setSend(boolean send) {
            isSend = send;
            return this;
        }

        public Builder setRead(boolean read) {
            isRead = read;
            return this;
        }

        public Builder setGroup(boolean group) {
            isGroup = group;
            return this;
        }

        public Builder setConversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder setConversationUserName(String conversationUserName) {
            this.conversationUserName = conversationUserName;
            return this;
        }

        public ChatBean builder() {
            return new ChatBean(this);
        }
    }
}
