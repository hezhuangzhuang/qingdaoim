package com.hw.messagemodule.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.service.KotlinMessageSocketService
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.message.IMessageModuleService
import com.hw.provider.user.UserContants

/**
 *author：pc-20171125
 *data:2020/1/15 10:09
 * 实际提供初始化的地方
 */
@Route(path = RouterPath.Chat.CHAT_MODULE_SERVICE)
class MessageModuleServiceImp : IMessageModuleService {

    /**
     * 初始化数据库
     */
    override fun initDb() {
        GreenDaoUtil.initDataBase()
    }

    /**
     * 发送数据
     */
    override fun sendMessage(message: MessageBody): Boolean {
        return KotlinMessageSocketService.sendMessage(message)
    }


    override fun init(context: Context?) {

    }
}