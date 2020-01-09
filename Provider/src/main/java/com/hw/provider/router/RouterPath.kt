package com.hw.provider.router

/**
 *author：Thinkpad
 *data:2019/12/7 20:17
 */
object RouterPath {
    //用户模块
    class UserCenter{
        companion object{
            const val PATH_LOGIN="/userCenter/login"
        }
    }

    //主模块
    class Main{
        companion object{
            const val PATH_MAIN="/main/main"
        }
    }

    //通讯录模块
    class Contacts{
        companion object{
            const val CONTACTS_MODULE_SERVICE="/contacts/service"
        }
    }

    //会议模块
    class Conf{
        companion object{
            const val CREATE_CONF="/conf/createConf"
        }
    }
}