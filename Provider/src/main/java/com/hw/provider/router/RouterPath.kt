package com.hw.provider.router

/**
 *author：Thinkpad
 *data:2019/12/7 20:17
 */
object RouterPath {
    //用户模块
    class UserCenter {
        companion object {
            const val PATH_LOGIN = "/userCenter/login"
        }
    }

    //主模块
    class Main {
        companion object {
            const val PATH_MAIN = "/main/main"
        }
    }

    //通讯录模块
    class Contacts {
        companion object {
            //联系人详情
            const val CONTACT_DETAILS="/contacts/contactsDetails"
            const val FILED_RECEIVE_ID = "FILED_RECEIVE_ID"
            const val FILED_RECEIVE_NAME = "FILED_RECEIVE_NAME"

            //提供给其他模块调用的服务
            const val CONTACTS_MODULE_SERVICE = "/contacts/service"
        }
    }

    //会议模块
    class Conf {
        companion object {
            const val CREATE_CONF = "/conf/createConf"
        }
    }

    //聊天模块
    class Chat {
        companion object {
            const val CHAT = "/chat/chat"

            //提供给其他模块调用的服务
            const val CHAT_MODULE_SERVICE = "/chat/db"

            const val FILED_RECEIVE_ID = "FILED_RECEIVE_ID"
            const val FILED_RECEIVE_NAME = "FILED_RECEIVE_NAME"
            const val FILED_IS_GROUP = "FILED_IS_GROUP"

        }
    }

    //华为会议模块
    class Huawei {
        companion object {
            const val HUAWEI_MODULE_SERVICE = "/huawei/service"
        }
    }

}