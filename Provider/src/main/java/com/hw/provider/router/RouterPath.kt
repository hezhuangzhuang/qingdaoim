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
            const val PATH_ABOUT = "/userCenter/about"
            const val USER_MODULE_SERVICE = "/userCenter/service"
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

            const val MY_CONF_LIST = "/conf/myConfList"

            const val SELECTED_PEOPLE = "/conf/selectedPeople"

            const val SELECTED_PEOPLE_REQUEST = 0X111

            //已选择的成员
            const val FILED_SELECTED_PEOPLE = "FILED_SELECTED_PEOPLE"




            //是否是历史会议
            const val FILED_IS_HISTORY = "FILED_IS_HISTORY"

            //是否是创建群组
            const val FILED_IS_CREATE_GROUP = "FILED_IS_CREATE_GROUP"

            //是否是视频会议
            const val FILED_VIDEO_CONF = "FILED_VIDEO_CONF"
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

            const val CHAIR_SELECT = "/huawei/chairSelect"

            //在线的会场
            const val FILED_ONLINE_LIST = "FILED_ONLINE_LIST"

            //会议id
            const val FILED_SMC_CONF_ID = "FILED_SMC_CONF_ID"
        }
    }

}